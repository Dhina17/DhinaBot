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

import io.github.dhina17.tgbot.utils.ProgressUtils;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;

public class DriveUploadProgressListener implements MediaHttpUploaderProgressListener {

    private MessageQueue messageQueue;
    Boolean isEdited = false;

    public DriveUploadProgressListener(MessageQueue mQueue) {
        this.messageQueue = mQueue;
    }

    @Override
    public void progressChanged(MediaHttpUploader uploader) throws IOException {
        Long fileSizeInBytes = uploader.getMediaContent().getLength();
        String fileSize = ProgressUtils.getSizeinMB(fileSizeInBytes);
        switch (uploader.getUploadState()) {
            case NOT_STARTED:
                // Do Nothing
                break;

            case INITIATION_STARTED:
                messageQueue.addEdit("<b>🔄 Uploading initiating...</b>");
                break;

            case INITIATION_COMPLETE:
                // Do Nothing
                break;

            case MEDIA_IN_PROGRESS:{
                // Get the progress percent
                String progressInPercent = NumberFormat.getPercentInstance().format(uploader.getProgress());
                int uploadedPercent = Integer.parseInt(progressInPercent.split("%")[0]);
            
                // Get the progress in data
                String progressInData = ProgressUtils.getPercentValue(fileSizeInBytes, uploadedPercent);

                // Just to avoid the delay of uploading the file.
                // Actually upload completes faster but showing the progress will take time.
                // TO DO: Will fix this in a better way later.
                if(!isEdited && uploadedPercent != 0 && uploadedPercent % 10 == 0){
                    String progress = "🔺 <b>Uploading :</b>\n<b>🕖 Progress :</b> <code>" + progressInData + " / " + fileSize + " MB</code>";
                    messageQueue.addEdit(progress);
                    isEdited=true;
                }else if(uploadedPercent != 0 && uploadedPercent % 10 != 0){
                    isEdited = false;
                }

                
                break;
            }

               
            case MEDIA_COMPLETE:
                // Clear the queue as soon as the upload completed.
                messageQueue.getQueue().clear();
                messageQueue.addEdit("✅ <b>Upload completed.</b>");
                break;
        }

    }

}
