//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

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

import io.github.dhina17.tgbot.model.Result;
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
        return ProgressUtils.correctFileName(fileName);
    }

    /**
     * 
     * @param messageQueue Queue which contains all bot methods to be executed
     * @param link The link of the file to be downloaded
     * @return Result obj
     */
    public static Result downloadFile(MessageQueue messageQueue, String link){
        
        Result result = new Result();
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
            long startTime = System.currentTimeMillis();

            while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
                downloaded += bytesRead;

                // Get the downloaded data in MB
                String downloadedSize = ProgressUtils.getSizeinMB(downloaded.longValue());

                /* Show updates once per 3.160 seconds | 60 / 19 = 3.16 (approx). Run 19 times/minute */
                long currentTime = System.currentTimeMillis();
                if(currentTime - startTime > 3160) {
                    String progress = "🔻 <b>Downloading :</b> <code>" + fileName + "</code>\n<b>🕖 Progress :</b> <code>" + downloadedSize + " / " + fileSize + " MB</code>";
                    messageQueue.addEdit(progress);
                    startTime = currentTime;
                }
            }

            //Closing output stream
            out.close();

            // Closing input stream
            in.close();

            // final result
            result.setIsSuccess(true);
            result.setFileName(fileName);
            
        }catch(IOException e){
            LOGGER.error("Download failed", e);
        }
        return result;
    }  
}
