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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import io.github.dhina17.tgbot.GdriveConfig;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class DriveUtils {
    public static final String APP_NAME = "DhinaBot";
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    // This Code is taken from DriveAPI java kickstart. We will move to a better way soon.

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DriveUtils.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline")
                        .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APP_NAME)
                .build();

        return service;
    }



    /**
     * 
     * @param bot  An Ability bot instance to execute bot API method - EditMessageText
     * @param editMsge  The Message to be updated to show the process
     * @param fileName  Name of the file which will be uploaded
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

            // Create a driveAPI service (For the first time, Need to authorize from your side)
            Drive drive = getDriveService();

            // Initialize the create 
            Create create = drive.files().create(fileMetaData, fileContent);
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
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
}