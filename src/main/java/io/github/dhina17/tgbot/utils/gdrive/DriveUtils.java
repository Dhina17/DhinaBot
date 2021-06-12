/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package io.github.dhina17.tgbot.utils.gdrive;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Random;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.configs.GdriveConfig;
import io.github.dhina17.tgbot.model.Result;
import io.github.dhina17.tgbot.utils.ProgressUtils;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class DriveUtils {
    
    // Static drive service
    public static Drive driveService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DriveUtils.class);

    /**
     * 
     * @param messageQueue Queue which contains all bot methods to be executed 
     * @param filePath  Path of the file which will be uploaded
     * @return  Result obj
     */
    public static Result uploadToDrive(MessageQueue messageQueue, String filePath) {
        Result result = new Result();
        java.io.File uploadFile = new java.io.File(filePath);
        
        /*
         *  Create file Meta-data and Set parent as TeamDrive if USE_TEAM_DRIVE is true
         */
        File fileMetaData = new File();

        // Set parent as Team drive if you want to use team drive.
        if(GdriveConfig.USE_TEAM_DRIVE){
            fileMetaData.setParents(Collections.singletonList(GdriveConfig.TEAM_DRIVE_ID));
        }

        /**
         * Set the file name by checking for duplicate names
         */
        fileMetaData.setName(setNameForFile(uploadFile.getName()));

        // Upload process
        try {
            // Get the file content
            InputStreamContent fileContent = new InputStreamContent(null, 
                                                                        new BufferedInputStream(
                                                                            new FileInputStream(uploadFile)));
            fileContent.setLength(uploadFile.length());

            // Initialize the create 
            Create create = driveService.files().create(fileMetaData, fileContent);
            create.setSupportsAllDrives(GdriveConfig.USE_TEAM_DRIVE); // Team drive
            create.setFields("name,size,webViewLink"); // set required fields from the response

            /**
             * Uploader
             */

            MediaHttpUploader uploader = create.getMediaHttpUploader();

            // For Resumable Upload.
            uploader.setDirectUploadEnabled(false); // DirectUpload is disabled
            uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);
            
            // Add our Progress Listerner
            uploader.setProgressListener(new DriveUploadProgressListener(messageQueue, uploadFile.getName()));

            // Create the file
            File uploadedFile = create.execute();

            // For our result
            result.setIsSuccess(true);
            result.setFileName(uploadedFile.getName());;
            result.setFileSize(ProgressUtils.getSizeinMB(uploadedFile.getSize()));;
            result.setFileLink(uploadedFile.getWebViewLink());

            // Delete the local file. We don't need this anymore
            uploadFile.delete();
        } catch (IOException e) {
            LOGGER.error("Upload Failed",e);
        }
        return result;
    }

    /**
     * 
     * @param messageQueue Queue which contains all bot methods to be executed
     * @param fileId Fileid of the file to be downloaded
     * @return Result
     */
	public static Result downloadFromDrive(MessageQueue messageQueue, String fileId) {
        Result result = new Result();
        try {
            // Create the request
            Drive.Files.Get request = driveService.files()
                                                .get(fileId)
                                                .setSupportsAllDrives(true)
                                                .setFields("name,size"); // Name and size are enough
            
            // Execute and get the file metadata
            File downloadFile = request.execute();
            String fileName = downloadFile.getName();
            Long fileSize = downloadFile.getSize();

            // Download the file
            OutputStream out = new FileOutputStream(fileName);
            request.getMediaHttpDownloader().setProgressListener(
                            new DriveDownloadProgressListener(messageQueue, fileName, fileSize));
            request.executeMediaAndDownloadTo(out);
            
            // Finalize the result
            result.setIsSuccess(true);
            result.setFileName(fileName);

		} catch (IOException e) {
			LOGGER.error("Download failed",e);
        }
        return result;
	}

    /**
     * 
     * Search for files with fileName in our drive.
     * If file found, rename the file which will be uploaded.
     * This way we can avoid the situation where many files have same file name.
     * 
     * @param fileName
     * @return String name
     */
    private static String setNameForFile(String fileName) {

        String name = fileName;
        String pageToken = null;

        try {

            /**
             * Create List with required fields.
             */
            List fileList = driveService.files().list()
                .setQ(String.format("name='%s'", fileName)) // Query for file with fileName
                .setFields("nextPageToken, files(id, name)");

            /**
             * If we are using shared drive, then set
             * - corpora = drive (searching files only in specified drive)
             * - supportsAllDrives and IncludeItemsFromAllDrives must be set to true
             *   when corpora == drive or allDrives
             */
            if (GdriveConfig.USE_TEAM_DRIVE) {
                fileList.setCorpora("drive")
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .setDriveId(GdriveConfig.TEAM_DRIVE_ID);
            }

            do {

                /**
                 * Execute the list with pagetoken.
                 */
                FileList searchResult = fileList
                    .setPageToken(pageToken)
                    .execute();
                
                /**
                 * If the search list response doesn't have empty Files list,
                 * Rename the current file with random 3 digit number prefix.
                 */
                if (!searchResult.getFiles().isEmpty()) {
                    Random random = new Random();
                    name =  random.nextInt(1000) + "-" + fileName;
                    break;
                }

                /**
                 * Get the next page token
                 */
                pageToken = searchResult.getNextPageToken();

            } while (pageToken != null);

        } catch (IOException e) {
            LOGGER.error("Failed to search files in drive", e);
        }

        return name;
    }
}