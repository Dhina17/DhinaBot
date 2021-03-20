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

    public Result(){

    }

    public Result(Boolean isSuccess, String fileName){
        this.isSuccess = isSuccess;
        this.fileName = fileName;
    }

    public Result(Boolean isSuccess, String fileName, String fileSize){
        this.isSuccess = isSuccess;
        this.fileName = fileName;
        this.fileSize = fileSize;
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

    public Boolean getIsSuccess() {
        return isSuccess;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

}
