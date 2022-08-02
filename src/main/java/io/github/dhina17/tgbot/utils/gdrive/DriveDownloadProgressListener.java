//
// DhinaBot - A simple telegram bot
// Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils.gdrive;

import java.io.IOException;
import java.text.NumberFormat;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;

import io.github.dhina17.tgbot.utils.ProgressUtils;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class DriveDownloadProgressListener implements MediaHttpDownloaderProgressListener {

    private MessageQueue messageQueue;
    private String fileName;
    private Long fileSizeInBytes;
    private long startTime;

    public DriveDownloadProgressListener(MessageQueue mQueue, String filename, Long filesize) {
        this.messageQueue = mQueue;
        this.fileName = filename;
        this.fileSizeInBytes = filesize;
    }

    @Override
    public void progressChanged(MediaHttpDownloader downloader) throws IOException {
        String fileSize = ProgressUtils.getSizeinMB(fileSizeInBytes);
        switch (downloader.getDownloadState()) {
            case NOT_STARTED:
                startTime = System.currentTimeMillis();
                break;
            case MEDIA_IN_PROGRESS: {
                // Get the progress percent
                String progressInPercent = NumberFormat.getPercentInstance().format(downloader.getProgress());
                int downloadedPercent = Integer.parseInt(progressInPercent.split("%")[0]);

                // Get the progress in data
                String progressInData = ProgressUtils.getPercentValue(fileSizeInBytes, downloadedPercent);

                /* Show updates once per 3.160 seconds | 60 / 19 = 3.16 (approx). Run 19 times/minute */
                long currentTime = System.currentTimeMillis();
                if(currentTime - startTime > 3160) {
                    String progress = "🔺 <b>Downloading : </b><code>" + fileName + "</code>\n<b>🕖 Progress :</b> <code>"
                            + progressInData + " / " + fileSize + " MB</code>";
                    messageQueue.addEdit(progress);
                    startTime = currentTime;
                }
                break;
            }

            case MEDIA_COMPLETE:
                // Clear the queue as soon as the download completed.
                messageQueue.getQueue().clear();
                break;
        }
    }

}
