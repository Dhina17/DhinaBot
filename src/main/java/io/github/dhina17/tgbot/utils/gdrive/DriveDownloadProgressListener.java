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
