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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import io.github.dhina17.tgbot.GdriveConfig;
import io.github.dhina17.tgbot.utils.gdrive.DriveUtils;
import io.github.dhina17.tgbot.utils.FileUtils;

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
                    .input(1)
                    .privacy(Privacy.ADMIN)  // Only admins can access this ability.
                    .locality(Locality.GROUP)  // This will work in groups.
                    .action(consumer -> {
                        Long chatId = consumer.chatId();
                        Update upd = consumer.update();
                        Message commandMessage = upd.getMessage();

                        // Split the command to get the Download file link
                        String[] commandMessageTexts = commandMessage.getText().split("/mirror");

                        SendMessage message = new SendMessage();
                        message.setChatId(String.valueOf(chatId));
                        message.setReplyToMessageId(commandMessage.getMessageId());

                        // Check for the link
                        if(commandMessageTexts.length > 0 && (commandMessageTexts[1].contains("https://") || commandMessageTexts[1].contains("http://"))){
                            String downloadUrl = commandMessageTexts[1];
                            message.setText("Getting Info...");
                            String fileName = FileUtils.getFileNameFromLink(downloadUrl);

                            try {
								bot.executeAsync(message, new SentCallback<Message>(){

									@Override
									public void onResult(BotApiMethod<Message> method, Message response) {
                                        Integer editMsgeId = response.getMessageId();
                                        EditMessageText editMsge = new EditMessageText();
                                        editMsge.setChatId(String.valueOf(chatId));
                                        editMsge.setMessageId(editMsgeId);
                                        Boolean isFileDownloaded = false;
                                        Boolean isFileUploaded = false;

                                        // Dowloading the file in async way
                                        CompletableFuture<Boolean> downloadProcess = CompletableFuture.supplyAsync(() -> {
                                            return FileUtils.downloadFile(bot, editMsge, downloadUrl, fileName);
                                        });

                                        try {
                                            isFileDownloaded = downloadProcess.get();
									    } catch (InterruptedException e) {
									        e.printStackTrace();
									    } catch (ExecutionException e) {
										    e.printStackTrace();
                                        }

                                        if(isFileDownloaded){
                                            try{
                                                // Upload the file after getting response from the download process
                                                CompletableFuture<String[]> uploadProcess = downloadProcess.thenApply( isDownloaded -> {
                                                    if(!isDownloaded){
                                                        String[] result = {"false", ""};
                                                        editMsge.setText("Download failed..");
                                                        try {
														    bot.execute(editMsge);
													    } catch (TelegramApiException e) {
														    e.printStackTrace();
													    }
                                                        return result;
                                                    }else{
                                                        return DriveUtils.uploadToDrive(bot, editMsge, fileName);
                                                    }
                                                });

                                                isFileUploaded = Boolean.parseBoolean(uploadProcess.get()[0]);
                                                if(!isFileUploaded){
                                                    editMsge.setText("Upload failed..");
                                                    bot.execute(editMsge);
                                                }else{
                                                    // Delete the Progress Message from the bot
                                                    DeleteMessage dMsge = new DeleteMessage(String.valueOf(chatId), editMsgeId);
                                                    bot.execute(dMsge);

                                                    // Sending the final sucess message with mirror link and link requested user
                                                    SendMessage successMessage = new SendMessage();
                                                    successMessage.setChatId(String.valueOf(chatId));
                                                    successMessage.setParseMode(ParseMode.HTML);

                                                    // Finalize the mirror link
                                                    String mirrorLink = GdriveConfig.GDRIVE_INDEX_LINK + uploadProcess.get()[1];
                                                    String reqUserName = commandMessage.getFrom().getUserName(); // Get the mirror link requested user id

                                                    // Finalize the message text
                                                    successMessage.setText(fileName + "\n\n" + "<a href=\"" + mirrorLink + "\">Shareable link</a>\n\n" + "To: @" + reqUserName);
                                                    // send the final message
                                                    bot.execute(successMessage);
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
                            message.setText("Give valid file link to mirror");
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
