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

package io.github.dhina17.tgbot.abilities;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import io.github.dhina17.tgbot.configs.GdriveConfig;
import io.github.dhina17.tgbot.utils.FileUtils;
import io.github.dhina17.tgbot.utils.botapi.BotExecutor;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;
import io.github.dhina17.tgbot.utils.gdrive.DriveUtils;
import io.github.dhina17.tgbot.utils.tgclient.TgClientUtils;

public class DriveMirror implements AbilityExtension{

    private AbilityBot bot;

    public DriveMirror(AbilityBot bot) {
        this.bot = bot;
    }

    public Ability mirror(){
        return Ability
                    .builder()
                    .name("mirror")
                    .info("Mirror the given link to gdrive")
                    .privacy(Privacy.ADMIN)  // Only admins can access this ability.
                    .locality(Locality.GROUP)  // This will work in groups.
                    .action(consumer -> {
                        Long chatId = consumer.chatId();
                        Update upd = consumer.update();
                        Message commandMessage = upd.getMessage();
                        Boolean isReply = commandMessage.isReply();
                        Message replyToMessage = commandMessage.getReplyToMessage();
                        String fileId = null;
                        String docFileName = null;
                        String downloadUrl = null;
                        Boolean isGdriveLink = false;

                        if(isReply){
                            if(replyToMessage.hasDocument()){
                                Document doc = replyToMessage.getDocument();
                                fileId = doc.getFileId();
                                docFileName = doc.getFileName();
                            }else if(replyToMessage.hasAudio()){
                                Audio audio = replyToMessage.getAudio();
                                fileId = audio.getFileId();
                                docFileName = audio.getFileName();
                            }else if(replyToMessage.hasVideo()){
                                Video video = replyToMessage.getVideo();
                                fileId = video.getFileId();
                                docFileName = video.getFileName();
                            }
                        }else{
                            // Split the command to get the Download file link
                            String[] commandMessageTexts = commandMessage.getText().split(" ");
                            if(commandMessageTexts.length > 1 && (commandMessageTexts[1].contains("https://") || commandMessageTexts[1].contains("http://"))){
                                String url = commandMessageTexts[1];
                                // Check for Gdrive file link . [TODO: Folders]
                                if(url.contains("https://drive.google.com/file/")){
                                    String[] splitUrl = url.split("/");
                                    downloadUrl = splitUrl[5];
                                    isGdriveLink = true;
                                }else{
                                    downloadUrl = commandMessageTexts[1];
                                }
                            }
                        }

                        SendMessage message = new SendMessage();
                        message.setChatId(String.valueOf(chatId));
                        message.setReplyToMessageId(commandMessage.getMessageId());
                        message.setParseMode(ParseMode.HTML);

                        // Check for the link
                        if(downloadUrl != null || fileId != null){
                            message.setText("<b>Getting info...</b>");
                            final String remoteFileId = fileId;
                            final String dUrl = downloadUrl;
                            final Boolean isGdriveUrl = isGdriveLink;
                            final String docName = docFileName;

                            try {
								bot.executeAsync(message, new SentCallback<Message>(){

									@Override
									public void onResult(BotApiMethod<Message> method, Message response) {
                                        Integer editMsgeId = response.getMessageId();

                                        // Create MessageQueue
                                        MessageQueue messageQueue = new MessageQueue()
                                                                                    .setChatId(String.valueOf(chatId))
                                                                                    .setMessageId(editMsgeId);

                                        // Create the executor and start it
                                        BotExecutor botExecutor = new BotExecutor(bot, messageQueue);
                                        botExecutor.start();

                                        // Send message queue to the update handler
                                        TgClientUtils.updateHandler.setMessageQueue(messageQueue);

                                        Boolean isFileDownloaded = false;
                                        Boolean isFileUploaded = false;

                                        // Dowloading the file in async way
                                        CompletableFuture<String[]> downloadProcess = CompletableFuture.supplyAsync(() -> {
                                            if(isReply){
                                                return TgClientUtils.dowloadFile(remoteFileId, docName);
                                            }else{
                                                if(isGdriveUrl){
                                                    return DriveUtils.downloadFromDrive(messageQueue, dUrl);
                                                }else{
                                                    return FileUtils.downloadFile(messageQueue, dUrl);
                                                }
                                            }
                                            
                                        });

                                        try {
                                            isFileDownloaded = Boolean.parseBoolean(downloadProcess.get()[0]);
									    } catch (InterruptedException e) {
									        e.printStackTrace();
									    } catch (ExecutionException e) {
										    e.printStackTrace();
                                        }

                                        if(isFileDownloaded){
                                            try{
                                                // Upload the file after getting response from the download process
                                                CompletableFuture<String[]> uploadProcess = downloadProcess.thenApply( downloadResult -> {
                                                    // Clear the queue as soon as the download process completed.
                                                    messageQueue.getQueue().clear();
                                                    Boolean isDownloaded = Boolean.parseBoolean(downloadResult[0]);
                                                    String downloadedFilePath = downloadResult[1];
                                                    if(!isDownloaded){
                                                        String[] result = {"false", ""};
                                                        messageQueue.addEdit("❗️<b>Download failed.</b>");
                                                        messageQueue.add("ENDS"); // To tell the excecutor this is the end(String object matters regardess of what text it is)
                                                        return result;
                                                    }else{
                                                        return DriveUtils.uploadToDrive(messageQueue, downloadedFilePath);
                                                    }
                                                });

                                                isFileUploaded = Boolean.parseBoolean(uploadProcess.get()[0]);
                                                if(!isFileUploaded){
                                                    messageQueue.addEdit("❗️<b>Upload failed.</b>");
                                                    messageQueue.add("ENDS"); // To tell the executor this is end.
                                                }else{
                                                    // Get the file name and file size
                                                    String fileName = uploadProcess.get()[1];
                                                    String fileSize = uploadProcess.get()[2];

                                                    // Delete the Progress Message from the bot
                                                    DeleteMessage dMsge = new DeleteMessage(String.valueOf(chatId), editMsgeId);
                                                    messageQueue.add(dMsge);

                                                    // Sending the final sucess message with mirror link and link requested user
                                                    SendMessage successMessage = new SendMessage();
                                                    successMessage.setChatId(String.valueOf(chatId));
                                                    successMessage.setParseMode(ParseMode.HTML);
                                                    successMessage.setDisableWebPagePreview(true);

                                                    // Finalize the mirror link
                                                    String mirrorLink = GdriveConfig.GDRIVE_INDEX_LINK + uploadProcess.get()[1];
                                                    String reqUserName = commandMessage.getFrom().getUserName(); // Get the mirror link requested user id

                                                    // Finalize the message text
                                                    String successText = "🔰 <b>FileName :</b> <code>" + fileName + "</code>\n\n" +
                                                                                "💾 <b>Size :</b> <code>" + fileSize + " MB</code>\n\n" +
                                                                                "🔗 <b>Link :</b> <a href=\"" + mirrorLink + "\">Here</a>\n\n" +
                                                                                "👤 <b>To :</b> @" + reqUserName; 
                                                    successMessage.setText(successText);
                                                    // send the final message
                                                    messageQueue.add(successMessage);
                                                    messageQueue.add("ENDS"); // To tell the executor this is end.
                                                }
                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }
                                        }
									}

									@Override
									public void onError(BotApiMethod<Message> method,
											TelegramApiRequestException apiException) {
                                        apiException.printStackTrace();
									}

									@Override
									public void onException(BotApiMethod<Message> method, Exception exception) {
                                        exception.printStackTrace();
									}

								});
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
                        }else{
                            message.setText("🔪 <b>Reply to a file or give link to mirror.</b>");
                            try {
								bot.execute(message);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
                        }
                    })
                    .build();
    }
}
