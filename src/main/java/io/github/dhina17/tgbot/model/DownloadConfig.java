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
