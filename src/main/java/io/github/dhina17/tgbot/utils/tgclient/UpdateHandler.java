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

package io.github.dhina17.tgbot.utils.tgclient;

import java.util.List;

import io.github.dhina17.tgbot.utils.ProgressUtils;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;
import it.tdlight.common.UpdatesHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.Object;
import it.tdlight.jni.TdApi.UpdateAuthorizationState;

public class UpdateHandler implements UpdatesHandler {
    
    private MessageQueue messageQueue;
    private Boolean isDownloadingCompleted = false;
    private Boolean isDownloadingActive = false;
    private String filePath = null;
    private Boolean isEdited = false;

    @Override
    public void onUpdates(List<Object> object) {
        for (int i = 0; i < object.size(); i++) {
            switch (object.get(i).getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR: {
                    AuthorizationUpdate
                            .onAuthorizationStateUpdated(((UpdateAuthorizationState) object.get(i)).authorizationState);
                    break;
                }

                case TdApi.UpdateFile.CONSTRUCTOR: {
                    TdApi.UpdateFile updateFile = (TdApi.UpdateFile) object.get(i);
                    isDownloadingCompleted = updateFile.file.local.isDownloadingCompleted;
                    isDownloadingActive = updateFile.file.local.isDownloadingActive;

                    // Get the expected file size
                    Integer fileSizeinBytes = updateFile.file.expectedSize;
                    String fileSize = ProgressUtils.getSizeinMB(fileSizeinBytes.longValue());

                    // Get the downloaded file size
                    Integer dlFileSizeinBytes = updateFile.file.local.downloadedSize;
                    String dlFileSize = ProgressUtils.getSizeinMB(dlFileSizeinBytes.longValue());

                    int downloadedPercent = ProgressUtils.getPercent(dlFileSizeinBytes.longValue(), fileSizeinBytes.longValue());

                    if(!isDownloadingCompleted){
                        // Just to avoid the delay of downloading the file.
                        // Actually download completes faster but showing the progress will take time.
                        // Will fix this in a better way later.
                        if(!isEdited && downloadedPercent != 0 && downloadedPercent % 10 == 0){
                            String process = "🔻 <b>Downloading :</b>\n<b>🕖 Progress :</b> <code>" + dlFileSize + " / " + fileSize + " MB</code>";
                            messageQueue.addEdit(process);
                            isEdited=true;
                        }else if(downloadedPercent != 0 && downloadedPercent % 10 != 0){
                            isEdited = false;
                        }
                    }

                    if(isDownloadingCompleted){
                        filePath = updateFile.file.local.path;
                    }
                }
            }
        }
    }

    public void setMessageQueue(MessageQueue mQueue){
        this.messageQueue = mQueue;
    }

    public Boolean[] getDownloadStatus(){
        Boolean[] status = {isDownloadingActive, isDownloadingCompleted};
        return status ;
    }

    public String getFilePath(){
        return filePath;
    }
    
}
