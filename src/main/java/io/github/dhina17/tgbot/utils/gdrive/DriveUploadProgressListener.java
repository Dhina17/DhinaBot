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

import java.io.IOException;
import java.text.NumberFormat;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class DriveUploadProgressListener implements MediaHttpUploaderProgressListener {

    private MessageQueue messageQueue;
    private StringBuilder sb = new StringBuilder("[");

    public DriveUploadProgressListener(MessageQueue mQueue) {
        this.messageQueue = mQueue;
    }

    @Override
    public void progressChanged(MediaHttpUploader uploader) throws IOException {
        Double fileSizeInMB = uploader.getMediaContent().getLength() * Math.pow(10, -6);
        String fileSize = String.format("%.2f", fileSizeInMB);
        switch (uploader.getUploadState()) {
            case NOT_STARTED:
                // Do Nothing
                break;

            case INITIATION_STARTED:
                messageQueue.addEdit("Uploading file initiating...");
                break;

            case INITIATION_COMPLETE:
                // Do Nothing
                break;

            case MEDIA_IN_PROGRESS:{
                // Get the progress percent
                String progress = NumberFormat.getPercentInstance().format(uploader.getProgress());
                
                sb.append("==");
                messageQueue.addEdit("Uploading...\n" + sb + "]\n" + progress + " of " + fileSize + "MB");
                break;
            }

               
            case MEDIA_COMPLETE:
                messageQueue.addEdit("Upload completed.... Generating shareable link..");
                break;
        }

    }

}
