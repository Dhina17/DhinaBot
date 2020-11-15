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

package io.github.dhina17.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;

public class DogbinUtils {
    
    public static final String DELDOG_URL = "https://del.dog/";

    public static String getDogbinUrl(String text) {

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

    public static String getPastedDeldogContent(String key) {

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
