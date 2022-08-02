//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import io.github.dhina17.tgbot.configs.BotConfig;

public class DocsUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocsUtils.class);
    
    public static final String getTextFromFile(AbilityBot bot, String fileId) {
		StringBuilder fileContent = new StringBuilder();
		String fileUrl = null;
		GetFile getFile = new GetFile();
		getFile.setFileId(fileId);

		try {
			File textFile = bot.execute(getFile);
			fileUrl = textFile.getFileUrl(BotConfig.BOT_TOKEN);
		} catch (TelegramApiException e) {
			LOGGER.error("Failed to execute the method",e);
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
				LOGGER.error("Failed to read the file",e1);
			}
		  }
		return fileContent.toString();
	  }
}


