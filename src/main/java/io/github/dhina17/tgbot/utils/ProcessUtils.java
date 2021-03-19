/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
    
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.utils.botapi.MessageQueue;
import io.github.dhina17.tgbot.utils.gdrive.DriveUtils;
import io.github.dhina17.tgbot.utils.tgclient.TgClientUtils;

public class ProcessUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);

    public static CompletableFuture<String[]> download(final String[] linkInfo, 
            MessageQueue messageQueue) {
                return CompletableFuture.supplyAsync(() -> {
                    if(Boolean.parseBoolean(linkInfo[0])){
                        return TgClientUtils.dowloadFile(linkInfo[1], linkInfo[2]);
                    }else{
                        if(Boolean.parseBoolean(linkInfo[4])){
                            return DriveUtils.downloadFromDrive(messageQueue, linkInfo[3]);
                        }else{
                            return FileUtils.downloadFile(messageQueue, linkInfo[3]);
                        }
                    }
                    
                });
    }

    public static CompletableFuture<String[]> upload(
            CompletableFuture<String[]> downloadProcess, MessageQueue messageQueue) {
                return downloadProcess.thenApply( downloadResult -> {
                    // Clear the queue as soon as the download process completed.
                    messageQueue.getQueue().clear();
                    Boolean isDownloaded = Boolean.parseBoolean(downloadResult[0]);
                    String downloadedFilePath = downloadResult[1];
                    if(!isDownloaded){
                        String[] result = {"false", ""};
                        messageQueue.addEdit("❗️<b>Download failed.</b>");
                        messageQueue.add("ENDS"); // To tell the excecutor this is the end(String object matters regardess of what text it is)
                        return result;
                    }else{
                        return DriveUtils.uploadToDrive(messageQueue, downloadedFilePath);
                    }
                });
    }

    public static String[] downloadAndUpload(final String[] linkInfo, MessageQueue messageQueue) {
        String[] result = {"false", "", ""};
        CompletableFuture<String[]> process = upload(download(linkInfo, messageQueue), 
                                                                messageQueue);
        
        try {
            result = process.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Process failed", e);
        }
        return result;
    }
}
