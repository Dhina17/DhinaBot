//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils.tgclient;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.model.Result;
import it.tdlight.common.ResultHandler;
import it.tdlight.jni.TdApi.DownloadFile;
import it.tdlight.jni.TdApi.File;
import it.tdlight.jni.TdApi.GetRemoteFile;
import it.tdlight.jni.TdApi.Object;

public class TgClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TgClientUtils.class);

    // Create update handler to get updates from Tdlib
    public static UpdateHandler updateHandler = new UpdateHandler();

    public static Result dowloadFile(String remoteFileId, String fileName) {
        Result result = new Result();
        GetRemoteFile getRemoteFile = new GetRemoteFile();
        getRemoteFile.remoteFileId = remoteFileId;
        TgClient.getClient().send(getRemoteFile, new ResultHandler() {

            @Override
            public void onResult(Object object) {
                // Only move further if query results TdApi.File object.
                if (object.getConstructor() == File.CONSTRUCTOR) {
                    File remoteFile = (File) object;
                    int remoteFileId = remoteFile.id;

                    // Set up the download file
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.fileId = remoteFileId;
                    downloadFile.offset = 0;
                    downloadFile.limit = 0;
                    downloadFile.synchronous = false;
                    downloadFile.priority = 1;

                    // Set the file name and file id before start the download process
                    updateHandler.setFileName(fileName);
                    updateHandler.setFileId(remoteFileId);

                    // Start the download
                    TgClient.getClient().send(downloadFile, new ResultHandler() {

                        @Override
                        public void onResult(Object object) {
                            // Do nothing
                        }

                    });
                }
            }
        });

        Boolean isDownloading = true;
        Boolean isDownloadCompleted = false;
        while (!isDownloadCompleted && isDownloading) {
            try {
                TimeUnit.SECONDS.sleep(5);
                Boolean[] downloadStatus = updateHandler.getDownloadStatus();
                isDownloading = downloadStatus[0];
                isDownloadCompleted = downloadStatus[1];
            } catch (InterruptedException e) {
                LOGGER.error("Failed to wait", e);
            }
        }
        result.setIsSuccess(isDownloadCompleted);
        if (isDownloadCompleted) {
            result.setFileName(updateHandler.getFilePath());
        }

        return result;
    }

}
