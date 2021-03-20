/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.github.dhina17.tgbot.model.DownloadConfig;
import io.github.dhina17.tgbot.utils.ProcessUtils;
import io.github.dhina17.tgbot.utils.botapi.BotExecutor;
import io.github.dhina17.tgbot.utils.botapi.MessageQueue;
import io.github.dhina17.tgbot.utils.tgclient.TgClientUtils;

public class DriveMirror implements AbilityExtension{

    private AbilityBot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(DriveMirror.class);

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
                        String fileName = null;
                        String downloadUrl = null;
                        Boolean isGdriveLink = false; 

                        if(isReply){
                            if(replyToMessage.hasDocument()){
                                Document doc = replyToMessage.getDocument();
                                fileId = doc.getFileId();
                                fileName = doc.getFileName();
                            }else if(replyToMessage.hasAudio()){
                                Audio audio = replyToMessage.getAudio();
                                fileId = audio.getFileId();
                                fileName = audio.getFileName();
                            }else if(replyToMessage.hasVideo()){
                                Video video = replyToMessage.getVideo();
                                fileId = video.getFileId();
                                fileName = video.getFileName();
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

                        // // Create a download config object
                        DownloadConfig downloadConfig = new DownloadConfig();
                        downloadConfig.setIsReply(isReply);
                        downloadConfig.setTgFileId(fileId);
                        downloadConfig.setTgFileName(fileName);
                        downloadConfig.setUrl(downloadUrl);
                        downloadConfig.setIsGdriveLink(isGdriveLink);


                        SendMessage message = new SendMessage();
                        message.setChatId(String.valueOf(chatId));
                        message.setReplyToMessageId(commandMessage.getMessageId());
                        message.setParseMode(ParseMode.HTML);

                        // Check for the link
                        if(downloadUrl != null || fileId != null){
                            message.setText("<b>Getting info...</b>");
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

                                        Boolean isFileUploaded = false;

                                        // Download and Upload the file
                                        String[] process = ProcessUtils.downloadAndUpload(downloadConfig,
                                                                        messageQueue);

                                        isFileUploaded = Boolean.parseBoolean(process[0]);
                                        
                                        if(isFileUploaded){
                                            // Get the file name and file size
                                            String fileName = process[1];
                                            String fileSize = process[2];

                                            // Delete the Progress Message from the bot
                                            DeleteMessage dMsge = new DeleteMessage(String.valueOf(chatId), editMsgeId);
                                            messageQueue.add(dMsge);

                                            // Sending the final sucess message with mirror link and link requested user
                                            SendMessage successMessage = new SendMessage();
                                            successMessage.setChatId(String.valueOf(chatId));
                                            successMessage.setParseMode(ParseMode.HTML);
                                            successMessage.setDisableWebPagePreview(true);

                                            // Finalize the mirror link
                                            String mirrorLink = GdriveConfig.GDRIVE_INDEX_LINK + fileName;
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
									}

									@Override
									public void onError(BotApiMethod<Message> method,
											TelegramApiRequestException apiException) {
                                            LOGGER.error("Failed to execute the method",apiException);
									}

									@Override
									public void onException(BotApiMethod<Message> method, Exception exception) {
                                        LOGGER.error("Failed to execute the method", exception);
									}

								});
							} catch (TelegramApiException e) {
								LOGGER.error("Failed to execute the method",e);
							}
                        }else{
                            message.setText("🔪 <b>Reply to a file or give link to mirror.</b>");
                            try {
								bot.execute(message);
							} catch (TelegramApiException e) {
								LOGGER.error("Failed to execute the method",e);
							}
                        }
                    })
                    .build();
    }
}
