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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DhinaBot extends AbilityBot {
	protected DhinaBot() {
		super(BotConfig.BOT_TOKEN, BotConfig.BOT_USERNAME);
	}

	@Override
  	public int creatorId() {
    	return BotConfig.CREATOR_ID;
  	}
  
  /*
   * Ability - Deldog
   * 
   * Reply /paste to a text message
   * 
   * It will provide the deldog link for the content of that text message
   * 
   */
  	public static final String DELDOG_URL = "https://del.dog/";

  	public Ability dogbinPaste() {
    	return Ability.builder()
                    .name("paste")
                    .info("Paste in dogbin")
                    .locality(Locality.ALL)  // This will work in all locality (user, groups).
                    .privacy(Privacy.CREATOR)  // Only creator can access this ability.
                    .action(consumer -> {
                    	Long chatId = consumer.chatId();
                    	Update upd = consumer.update();
                    	String textToPaste;
                    	String dogbinFinalUrl;
                    	String finalMessage;

                    	if(upd.getMessage().isReply() && upd.getMessage().getReplyToMessage().hasText()) {
                    		textToPaste = upd.getMessage().getReplyToMessage().getText();
                        	dogbinFinalUrl = getDogbinUrl(textToPaste);
                        	if(dogbinFinalUrl != null) {
                        		finalMessage = "Here you go..\n\ndeldog: " + dogbinFinalUrl;
                        	}else{
                        		finalMessage = "I can't reach del.dog \n Go and paste yourself😔";
                        	}  
                    	}else{
                        	finalMessage = "Reply to a message that contains text..Else No link for you..😂👊";
                    	}
                       
                      silent.send(finalMessage, chatId);
                    })
                    .build();
  	}

  	private String getDogbinUrl(String text) {

    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
                                      				.uri(URI.create(DELDOG_URL + "documents"))
                                      				.POST(BodyPublishers.ofString(text))
                                      				.build();
    	HttpResponse<?> response = null;
    	String finalContent = null;

    	try {
      		response = client.send(request, BodyHandlers.ofString());

      		if(response.statusCode() == 200) {
        		JSONObject responseBody = new JSONObject(response.body().toString());
        		finalContent = DELDOG_URL + responseBody.getString("key");
     		}  

    	}catch (IOException | InterruptedException e) {
      		e.printStackTrace();
    	}

    	return finalContent;
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
                        	String linkMessage;
                        	String content;
                        	String key;
                        	String finalMessage;
                        
                        	if(upd.getMessage().isReply() && upd.getMessage().getReplyToMessage().hasText() && upd.getMessage().getReplyToMessage().getText().contains(DELDOG_URL)) {
                          		linkMessage = upd.getMessage().getReplyToMessage().getText();
                          		key = linkMessage.split(DELDOG_URL)[1]; // Get the key of that dogbin url
                          		content = getPastedDeldogContent(key);
                          		if(content != null){
                            		finalMessage = "Got the content successfully. 👇 \n\n" + content;
                          		}else{
                            		finalMessage = "Sorry, I can't fetch the content 😢";
                          		}
                        	}else{
                          		finalMessage = "Reply to a message that contains Dogbin URL , otherwise no 🙁";
                        	}

                        	silent.send(finalMessage, chatId);

                       	})
                       .build();
    }

    private String getPastedDeldogContent(String key) {

      	HttpClient client = HttpClient.newHttpClient();
      	HttpRequest request = HttpRequest.newBuilder()
                                                    .uri(URI.create(DELDOG_URL + "raw/" + key))
                                                    .GET()
                                                    .build();
      	String finalContent = null;
     	try{
        	HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        	if (response.statusCode() == 200){
          		finalContent = response.body().toString();
        	}
      	}catch(IOException | InterruptedException e) {
        	e.printStackTrace();
      	}
                                                    
      	return finalContent;
    }

}
