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

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.model.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.configs.GdriveConfig;
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
     * @return  An array of String with two elements {"file uploaded or not", "Name of the file after uploading", "Size of the uploaded file"}
     */
    public static String[] uploadToDrive(MessageQueue messageQueue, String filePath) {
        String[] result = {"false", "", ""};
        java.io.File uploadFile = new java.io.File(filePath);
        
        /*
         *  Create file Meta-data and Set parent as TeamDrive if USE_TEAM_DRIVE is true
         */
        File fileMetaData = new File();

        // Set parent as Team drive if you want to use team drive.
        if(GdriveConfig.USE_TEAM_DRIVE){
            fileMetaData.setParents(Collections.singletonList(GdriveConfig.TEAM_DRIVE_ID));
        }

        fileMetaData.setName(uploadFile.getName());

        // Upload process
        try {
            // Get the file content
            InputStreamContent fileContent = new InputStreamContent(null, 
                                                                        new BufferedInputStream(
                                                                            new FileInputStream(uploadFile)));
            fileContent.setLength(uploadFile.length());

            // Initialize the create 
            Create create = driveService.files().create(fileMetaData, fileContent);
            create.setSupportsTeamDrives(GdriveConfig.USE_TEAM_DRIVE); // Team drive
            create.setFields("name,size"); // set required fields from the response

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
            result[0] = "true";
            result[1] = uploadedFile.getName();
            result[2] = ProgressUtils.getSizeinMB(uploadedFile.getSize());

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
     * @return An array of String with two elements {"file downloaded or not", "Name of the file"}
     */
	public static String[] downloadFromDrive(MessageQueue messageQueue, String fileId) {
        String[] result = {"false", ""};
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
            result[0] = "true";
            result[1] = fileName;

		} catch (IOException e) {
			LOGGER.error("Download failed",e);
        }
        return result;
	}
}