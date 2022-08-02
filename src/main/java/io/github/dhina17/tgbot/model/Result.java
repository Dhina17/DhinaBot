//
// DhinaBot - A simple telegram bot
// Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.model;

public class Result {

    /**
     * Model Class for the Process Results
     */

    /**
     * Process successful or not
     */
    private Boolean isSuccess = false;

    /**
     * {Dowloaded,Uploaded} file name
     */
    private String fileName = null;

    /**
     * Size of the file
     */
    private String fileSize = null;

    /**
     * Gdrive WebViewLink of the file
     */
    private String fileLink = null;

    public Result(){

    }

    public Result(Boolean isSuccess, String fileName){
        this.isSuccess = isSuccess;
        this.fileName = fileName;
    }

    public Result(Boolean isSuccess, String fileName, String fileSize, String fileLink){
        this.isSuccess = isSuccess;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileLink = fileLink;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileLink(String link) {
        this.fileLink = link;
    }

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileLink() {
        return fileLink;
    }

}
