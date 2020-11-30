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

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.dhina17.tgbot.utils.DocsUtils;
import io.github.dhina17.tgbot.utils.DogbinUtils;

public class Dogbin implements AbilityExtension {

    private AbilityBot bot;

    public Dogbin(AbilityBot bot) {
        this.bot = bot;
    }

 /*
   * Ability - Deldog - paste
   * 
   * Reply /paste to a text message
   * 
   * It will provide the deldog link for the content of that text message
   * 
   */

  	public Ability dogbinPaste() {
    	return Ability.builder()
                    .name("paste")
                    .info("Paste in dogbin")
                    .locality(Locality.ALL)  // This will work in all locality (user, groups).
                    .privacy(Privacy.ADMIN)  // Only admins can access this ability.
                    .action(consumer -> {
                    	Long chatId = consumer.chatId();
						Update upd = consumer.update();
						Message commandMessage = upd.getMessage();
						Message replyToMessage = commandMessage.getReplyToMessage();
						int commandMessageId = commandMessage.getMessageId(); // Get the command message id
                    	String textToPaste = null;
                    	String dogbinFinalUrl = null;
						String finalMessage = "";
						if(commandMessage.isReply() && (replyToMessage.hasDocument() || replyToMessage.hasText())) {
							if (replyToMessage.hasDocument()) {
								Document doc = replyToMessage.getDocument();
								String fileMimeType = doc.getMimeType();
								if (fileMimeType.contains("text/")) {
									String fileId = doc.getFileId();
									textToPaste = DocsUtils.getTextFromFile(bot, fileId);
								} else {
									finalMessage = "Unsupported MIME type - " + fileMimeType + ". Sorry !";
								}
							}
							
							if(replyToMessage.hasText()) {
									textToPaste = replyToMessage.getText();
							}

							if (textToPaste != null) {
								dogbinFinalUrl = DogbinUtils.getDogbinUrl(textToPaste);

							    if (dogbinFinalUrl != null && !dogbinFinalUrl.isEmpty()) {
									finalMessage = "Here you go..\n\ndeldog: " + dogbinFinalUrl;
								} else {
									finalMessage = "I can't reach del.dog \n Go and paste yourself😔";
								}
							}
						} else {
							finalMessage = "Reply to a message that contains text..Else No link for you..😂👊";
						}

						SendMessage message = new SendMessage();
						message.setChatId(String.valueOf(chatId));
						message.setReplyToMessageId(commandMessageId); // Reply to the command message
						message.setText(finalMessage);

                        try {
                            bot.execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
					})
					.build( );
  	}

  /*
   * Ability - Deldog - getpaste
   * 
   * Reply /getpaste to a message contains dogbin url
   * 
   * It will give the content of that dogbin link
   * 
   */

	public Ability dogbinGetPaste() {
    	return Ability.builder()
                       .name("getpaste")
                       .info("Get content from a deldog url")
                       .locality(Locality.ALL)  // This will work in all locality (user, groups).
                       .privacy(Privacy.ADMIN) // Only admins can access this ability.
                       .action( consumer -> {
                    		Long chatId = consumer.chatId();
							Update upd = consumer.update();
							Message commandMessage = upd.getMessage();
                            Message replyToMessage = commandMessage.getReplyToMessage();
                            int commandMessageId = commandMessage.getMessageId(); // Get the command message id
                        	String linkMessage;
                        	String content;
                        	String key;
                        	String finalMessage;
                        
							if(commandMessage.isReply()
										&& replyToMessage.hasText()
										            && replyToMessage.getText().contains(DogbinUtils.DELDOG_URL)) {
                          		linkMessage = replyToMessage.getText();
                          		key = linkMessage.split(DogbinUtils.DELDOG_URL)[1]; // Get the key of that dogbin url
                          		content = DogbinUtils.getPastedDeldogContent(key);
                          		if(content != null){
                            		finalMessage = "Got the content successfully. 👇 \n\n" + content;
                          		}else{
                            		finalMessage = "Sorry, I can't fetch the content 😢";
                          		}
                        	}else{
                          		finalMessage = "Reply to a message that contains Dogbin URL , otherwise no 🙁";
							}

							SendMessage message = new SendMessage();
							message.setChatId(String.valueOf(chatId));
							message.setReplyToMessageId(commandMessageId); // Reply to the command message
							message.setText(finalMessage);
							
							try {
                                bot.execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }

						})
                       .build();
	}  
}
