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
import java.util.concurrent.TimeUnit;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public class DriveUploadProgressListener implements MediaHttpUploaderProgressListener {

    private AbilityBot bot;
    private EditMessageText editMsge;
    private StringBuilder sb = new StringBuilder();
    private int count = 0;

    public DriveUploadProgressListener(AbilityBot bot, EditMessageText editMsge) {
        this.bot = bot;
        this.editMsge = editMsge;
    }
	@Override
	public void progressChanged(MediaHttpUploader uploader) throws IOException {
        Double fileSizeInMB = uploader.getMediaContent().getLength() * Math.pow(10, -6);
        String fileSize = String.format("%.2f", fileSizeInMB);
        try{
            switch(uploader.getUploadState()){
                case NOT_STARTED:
                    // Do Nothing
                    break;

                case INITIATION_STARTED:
                    editMsge.setText("Uploading file initiating...");
                    bot.execute(editMsge);
                    sb.append("[");
                    TimeUnit.SECONDS.sleep(30); // Sleep 30 seconds to handle execeeding API limit.
                    break;

                case INITIATION_COMPLETE:
                    // Do Nothing
                    break;

                case MEDIA_IN_PROGRESS:
                    // Get the progress percent
                    String progress = NumberFormat.getPercentInstance().format(uploader.getProgress());
                    
                   //To avoid execeeding API limit, just show updates only for 10 times.Will handle it in a better way. 
                    if(count < 10){
                        sb.append("==");
                        editMsge.setText("Uploading...\n" + sb + "]\n" + progress + " % of " + fileSize + "MB");
                        bot.execute(editMsge);
                        count++;
                    }
                    
                    break;
                case MEDIA_COMPLETE:
                    editMsge.setText("Upload completed.... Generating shareable link..");
                    bot.execute(editMsge);
                    break;
                }
        }catch(Exception e){
            e.printStackTrace();
        } 
		
	}
    
}
