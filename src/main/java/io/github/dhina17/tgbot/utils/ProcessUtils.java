//
// DhinaBot - A simple telegram bot
// Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.model.DownloadConfig;
import io.github.dhina17.tgbot.model.Result;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;
import io.github.dhina17.tgbot.utils.gdrive.DriveUtils;
import io.github.dhina17.tgbot.utils.tgclient.TgClientUtils;

public class ProcessUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProcessUtils.class);

    public static CompletableFuture<Result> download(final DownloadConfig downloadConfig, 
            MessageQueue messageQueue) {
                return CompletableFuture.supplyAsync(() -> {
                    if(downloadConfig.getIsReply()){
                        return TgClientUtils.dowloadFile(downloadConfig.getTgFileId(), 
                                                                        downloadConfig.getTgFileName());
                    }else{
                        if(downloadConfig.getIsGdriveLink()){
                            return DriveUtils.downloadFromDrive(messageQueue, downloadConfig.getUrl());
                        }else{
                            return FileUtils.downloadFile(messageQueue, downloadConfig.getUrl());
                        }
                    }
                    
                });
    }

    public static CompletableFuture<Result> upload(
            CompletableFuture<Result> downloadProcess, MessageQueue messageQueue) {
                return downloadProcess.thenApply( downloadResult -> {
                    // Clear the queue as soon as the download process completed.
                    messageQueue.getQueue().clear();
                    Boolean isDownloaded = downloadResult.getIsSuccess();
                    String downloadedFilePath = downloadResult.getFileName();
                    if(!isDownloaded){
                        messageQueue.addEdit("❗️<b>Download failed.</b>");
                        messageQueue.add("ENDS"); // To tell the excecutor this is the end(String object matters regardess of what text it is)
                        return new Result();
                    }else{
                        return DriveUtils.uploadToDrive(messageQueue, downloadedFilePath);
                    }
                });
    }

    public static Result downloadAndUpload(final DownloadConfig downloadConfig, MessageQueue messageQueue) {
        Result result = new Result();
        CompletableFuture<Result> process = upload(download(downloadConfig, messageQueue), 
                                                                        messageQueue);
        
        try {
            result = process.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Process failed", e);
        }
        return result;
    }
}
