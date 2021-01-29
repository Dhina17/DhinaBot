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

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.dhina17.tgbot.configs.BotConfig;

public class DocsUtils {
    
    public static final String getTextFromFile(AbilityBot bot, String fileId) {
		StringBuilder fileContent = new StringBuilder();
		String fileUrl = null;
		GetFile getFile = new GetFile();
		getFile.setFileId(fileId);

		try {
			File textFile = bot.execute(getFile);
			fileUrl = textFile.getFileUrl(BotConfig.BOT_TOKEN);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

		if (fileUrl != null) {
			URL file = null;
			try {
                file = new URL(fileUrl);
                Scanner reader = new Scanner(file.openStream());
				while (reader.hasNextLine()) {
					fileContent.append(reader.nextLine());
					fileContent.append("\n");
				}
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		  }
		
		
			return fileContent.toString();
	
	  }
}


