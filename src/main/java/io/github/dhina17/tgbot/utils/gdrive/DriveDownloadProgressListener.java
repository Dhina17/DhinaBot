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
    private Boolean isEdited = false;

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
                // Do Nothing
                break;
            case MEDIA_IN_PROGRESS: {
                // Get the progress percent
                String progressInPercent = NumberFormat.getPercentInstance().format(downloader.getProgress());
                int downloadedPercent = Integer.parseInt(progressInPercent.split("%")[0]);

                // Get the progress in data
                String progressInData = ProgressUtils.getPercentValue(fileSizeInBytes, downloadedPercent);

                // Just to avoid the delay of downloading the file.
                // Actually download completes faster but showing the progress will take time.
                // TO DO: Will fix this in a better way later.
                if (!isEdited && downloadedPercent != 0 && downloadedPercent % 10 == 0) {
                    String progress = "🔺 <b>Uploading : </b><code>" + fileName + "</code>\n<b>🕖 Progress :</b> <code>"
                            + progressInData + " / " + fileSize + " MB</code>";
                    messageQueue.addEdit(progress);
                    isEdited = true;
                } else if (downloadedPercent != 0 && downloadedPercent % 10 != 0) {
                    isEdited = false;
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
