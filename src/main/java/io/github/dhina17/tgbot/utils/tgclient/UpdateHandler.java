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
    private StringBuilder sb = new StringBuilder("[");
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
                    int fileSizeinBytes = updateFile.file.expectedSize;
                    Double fileSizeinMB = fileSizeinBytes * Math.pow(10, -6);
                    String fileSize = String.format("%.2f", fileSizeinMB);

                    // Get the downloaded file size
                    int dlFileSizeinBytes = updateFile.file.local.downloadedSize;
                    Double dlFileSizeinMB = dlFileSizeinBytes * Math.pow(10, -6);
                    String dlFileSize = String.format("%.2f", dlFileSizeinMB);

                    int downloadedPercent = (int)(dlFileSizeinMB * 100 /fileSizeinMB);

                    if(!isDownloadingCompleted){
                        // Just to avoid the delay of downloading the file.
                        // Actually download completes faster but showing the progress will take time.
                        // Will fix this in a better way later.
                        if(!isEdited && downloadedPercent != 0 && downloadedPercent % 10 == 0){
                            sb.append("==");
                            String process = "Downloading : \n" + sb + "]\n" + dlFileSize + " / " + fileSize + " MB (" + downloadedPercent + " %)";
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
