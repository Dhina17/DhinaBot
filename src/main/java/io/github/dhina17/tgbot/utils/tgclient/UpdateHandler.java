//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

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
    private String fileName = null;
    private int fileId = 0;
    private long startTime = System.currentTimeMillis();

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
                    if (updateFile.file.id == fileId) {
                        isDownloadingCompleted = updateFile.file.local.isDownloadingCompleted;
                        isDownloadingActive = updateFile.file.local.isDownloadingActive;

                        // Get the expected file size
                        Long fileSizeinBytes = updateFile.file.expectedSize;
                        String fileSize = ProgressUtils.getSizeinMB(fileSizeinBytes);

                        // Get the downloaded file size
                        Long dlFileSizeinBytes = updateFile.file.local.downloadedSize;
                        String dlFileSize = ProgressUtils.getSizeinMB(dlFileSizeinBytes);

                        if(!isDownloadingCompleted){
                            /* Show updates once per 3.160 seconds | 60 / 19 = 3.16 (approx). Run 19 times/minute */
                            long currentTime = System.currentTimeMillis();
                            if(currentTime - startTime > 3160) {
                                String process = "🔻 <b>Downloading : </b><code>" + fileName + "</code>\n<b>🕖 Progress :</b> <code>" + dlFileSize + " / " + fileSize + " MB</code>";
                                messageQueue.addEdit(process);
                                startTime = currentTime;
                            }
                        }

                        if(isDownloadingCompleted){
                            filePath = updateFile.file.local.path;
                        }
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

    public void setFileName(String filename){
        this.fileName = filename;
    }
    
    public void setFileId(int id) {
        this.fileId = id;
    }
}
