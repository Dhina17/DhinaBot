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

package io.github.dhina17.tgbot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.dhina17.utils.DocsUtils;
import io.github.dhina17.utils.DogbinUtils;

public class DhinaBot extends AbilityBot {
	protected DhinaBot() {
		super(BotConfig.BOT_TOKEN, BotConfig.BOT_USERNAME);
	}

	@Override
  	public int creatorId() {
    	return BotConfig.CREATOR_ID;
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
                    .privacy(Privacy.CREATOR)  // Only creator can access this ability.
                    .action(consumer -> {
                    	Long chatId = consumer.chatId();
						Update upd = consumer.update();
						int replyToMessageId = upd.getMessage().getMessageId(); // Get the command message id
                    	String textToPaste = null;
                    	String dogbinFinalUrl = null;
                    	String finalMessage;

						if (upd.getMessage().isReply() && (upd.getMessage().getReplyToMessage().hasText()
							|| upd.getMessage().getReplyToMessage().hasDocument())) {

						if (upd.getMessage().getReplyToMessage().hasDocument()) {
							Document doc = upd.getMessage().getReplyToMessage().getDocument();
							String fileMimeType = doc.getMimeType();
							System.out.println(fileMimeType);
							if (fileMimeType.equals("text/plain")) {
								String fileId = doc.getFileId();
								textToPaste = DocsUtils.getTextFromFile(this, fileId);

							} else {
								finalMessage = "Unsupported MIME type - " + fileMimeType + ". Sorry !";
							}

						} else {
							textToPaste = upd.getMessage().getReplyToMessage().getText();
						}

						if (textToPaste != null)
							dogbinFinalUrl = DogbinUtils.getDogbinUrl(textToPaste);

						if (dogbinFinalUrl != null && !dogbinFinalUrl.isEmpty()) {
							finalMessage = "Here you go..\n\ndeldog: " + dogbinFinalUrl;
						} else {
							finalMessage = "I can't reach del.dog \n Go and paste yourself😔";
						}
					} else {
						finalMessage = "Reply to a message that contains text..Else No link for you..😂👊";
					}

					SendMessage message = new SendMessage();
					message.setChatId(String.valueOf(chatId));
					message.setReplyToMessageId(replyToMessageId); // Reply to the command message
					message.setText(finalMessage);

					silent.execute(message);

				}).build();
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
                       .privacy(Privacy.CREATOR) // Only creator can access this ability.
                       .action( consumer -> {
                    		Long chatId = consumer.chatId();
							Update upd = consumer.update();
							int replyToMessageId = upd.getMessage().getMessageId(); // Get the command message id
                        	String linkMessage;
                        	String content;
                        	String key;
                        	String finalMessage;
                        
							if(upd.getMessage().isReply()
										&& upd.getMessage().getReplyToMessage().hasText()
										            && upd.getMessage().getReplyToMessage().getText().contains(DogbinUtils.DELDOG_URL)) {
                          		linkMessage = upd.getMessage().getReplyToMessage().getText();
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
							message.setReplyToMessageId(replyToMessageId); // Reply to the command message
							message.setText(finalMessage);
							
							silent.execute(message);

						})
                       .build();
    }

}
