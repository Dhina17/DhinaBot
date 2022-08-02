//
// DhinaBot - A simple telegram bot
// Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.model;

public class DownloadConfig {
    /**
     * A model class for the download configs
     */


    /**
     * User replied to a telegram content or not.
     */
    private Boolean isReply = false;

    /**
     * Remote file id of the content , if the user replied to a telegram content.
     */
    private String tgFileId = null;

    /**
     * Name of the content, if the user replied to a telegram content.
     */
    private String tgFileName = null;

    /**
     * Download file direct url
     */
    private String url = null;

    /**
     * Provided Gdrive file link or not
     */
    private Boolean isGdriveLink = false; 

    public void setIsReply(Boolean isReply) {
        this.isReply = isReply;
    }

    public void setTgFileId(String tgFileId) {
        this.tgFileId = tgFileId;
    }

    public void setTgFileName(String tgFileName) {
        this.tgFileName = tgFileName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIsGdriveLink(Boolean isGdriveLink) {
        this.isGdriveLink = isGdriveLink;
    }

    public Boolean getIsReply() {
        return isReply;
    }

    public String getTgFileId() {
        return tgFileId;
    }

    public String getTgFileName() {
        return tgFileName;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getIsGdriveLink() {
        return isGdriveLink;
    }
}
