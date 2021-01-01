/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2020  Dhina17 <dhinalogu@gmail.com>
    
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
import java.io.IOException;
import java.util.Collections;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.model.File;

import io.github.dhina17.tgbot.GdriveConfig;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class DriveUtils {
    
    // Static drive service
    public static Drive driveService;

    /**
     * 
     * @param messageQueue Queue which contains all bot methods to be executed 
     * @param filePath  Path of the file which will be uploaded
     * @return  An Array of String with two elements {"file uploaded or not", "Name of the file after uploading"}
     */
    public static String[] uploadToDrive(MessageQueue messageQueue, String filePath) {
        String[] result = {"false", ""};
        java.io.File uploadFile = new java.io.File(filePath);
        
        /*
         *  Create file Meta-data and Set parent as TeamDrive
         *  If you don't want to upload file to TeamDrive, Just nuke setParents() or Pass your folder ID or Drive ID
         */
        File fileMetaData = new File();
        fileMetaData.setParents(Collections.singletonList(GdriveConfig.TEAM_DRIVE_ID));
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
            create.setSupportsTeamDrives(true); // Supports TeamDrive (Remove if you don't want)

            /**
             * Uploader
             */

            MediaHttpUploader uploader = create.getMediaHttpUploader();

            // For Resumable Upload.
            uploader.setDirectUploadEnabled(false); // DirectUpload is disabled
            uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);
            
            // Add our Progress Listerner
            uploader.setProgressListener(new DriveUploadProgressListener(messageQueue));

            // Create the file
            File uploadedFile = create.execute();

            // For our result
            result[0] = "true";
            result[1] = uploadedFile.getName();

            // Delete the local file. We don't need this anymore
            uploadFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}