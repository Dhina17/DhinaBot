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

package io.github.dhina17.tgbot.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static String getFileNameFromLink(String link) {
        String fileName = null;
        try {
            URL file = new URL(link);
            String filePath = file.getPath();
            fileName = filePath.substring(filePath.lastIndexOf('/')+1);

        } catch (MalformedURLException e) {
            LOGGER.error("Error with link",e);
        }
        return fileName;
    }

    /**
     * 
     * @param messageQueue Queue which contains all bot methods to be executed
     * @param link The link of the file to be downloaded
     * @return A Boolean value implies Download success or not
     */
    public static String[] downloadFile(MessageQueue messageQueue, String link){
        
        String[] result = {"false", ""};
        try{

            // Create URL
            URL url = new URL(link);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("HEAD");

            long fileSizeinBytes = httpConnection.getContentLengthLong();
            String fileSize = ProgressUtils.getSizeinMB(fileSizeinBytes);

            String fileName = getFileNameFromLink(link);
            File file = new File(fileName);

            BufferedInputStream in = new BufferedInputStream(url.openStream());
            FileOutputStream out = new FileOutputStream(file);
                
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            Integer downloaded = 0;
            int downloadedPercent = 0;

            Boolean isEdited = false;

            while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
                downloaded += bytesRead;

                // Get the downloaded data in MB
                String downloadedSize = ProgressUtils.getSizeinMB(downloaded.longValue());

                // Get the percent
                downloadedPercent = ProgressUtils.getPercent(downloaded.longValue(), fileSizeinBytes);
                    
                // Just to avoid the delay of downloading the file.(Actually download completes faster but showing the progress will take time)
                // Will fix this in a better way later.
                if(!isEdited && downloadedPercent != 0 && downloadedPercent % 10 == 0){
                    String progress = "🔻 <b>Downloading :</b> <code>" + fileName + "</code>\n<b>🕖 Progress :</b> <code>" + downloadedSize + " / " + fileSize + " MB</code>";
                    messageQueue.addEdit(progress);
                    isEdited=true;
                }else if(downloadedPercent != 0 && downloadedPercent % 10 != 0){
                    isEdited = false;
                }
            }

            //Closing output stream
            out.close();

            // Closing input stream
            in.close();

            // final result
            result[0] = "true";
            result[1] = file.getPath();
        }catch(IOException e){
            LOGGER.error("Download failed", e);
        }
        return result;
    }  
}
