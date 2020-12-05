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

package io.github.dhina17.tgbot.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public class FileUtils {
    public static String getFileNameFromLink(String link) {
        String fileName = null;
        try {
            URL file = new URL(link);
            String filePath = file.getPath();
            fileName = filePath.substring(filePath.lastIndexOf('/')+1);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 
     * @param bot An Ability bot instance to execute bot API method - EditMessageText
     * @param editMsge The Message to be updated to show the process
     * @param link The link of the file to be downloaded
     * @param fileName The Name of the file to be downloaded
     * @return A Boolean value implies Download success or not
     */
    public static Boolean downloadFile(AbilityBot bot, EditMessageText editMsge, String link, String fileName){
        try{

            // Create URL
            URL url = new URL(link);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("HEAD");

            long fileSizeinBytes = httpConnection.getContentLengthLong();
            Double fileSizeinMB = fileSizeinBytes * Math.pow(10, -6);
            String fileSize = String.format("%.2f", fileSizeinMB);

            File file = new File(fileName);

            try(BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                FileOutputStream out = new FileOutputStream(file);
                
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                int downloaded = 0;
                int downloadedPercent = 0;

                StringBuilder sb = new StringBuilder();
                sb.append("[");

                editMsge.setText("Getting ready for download...");
                bot.execute(editMsge);
                Boolean isEdited = false;

                while((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    out.write(dataBuffer, 0, bytesRead);
                    downloaded += bytesRead;

                    downloadedPercent = (int) ((downloaded * 100) / fileSizeinBytes);

                    // Telegram API won't allow >20 requests within a minute.To handle this, Show updates for only 10s percents.
                    // Will handle in a better way.
                    if(!isEdited && downloadedPercent != 0 && downloadedPercent % 10 == 0){
                        sb.append("==");
                        editMsge.setText("\rDownloading:\n\n"+fileName+"\n"+sb+"]\n"+downloadedPercent+"% of " + fileSize + " MB");
                        bot.execute(editMsge);
                        isEdited=true;
                    }else if(downloadedPercent != 0 && downloadedPercent % 5 != 0){
                        isEdited = false;
                    }
                }

                //Closing output stream
                out.close();
    
                return true;
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }

        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    } 
    
}
