//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import io.github.dhina17.tgbot.utils.gdrive.DriveService;
import io.github.dhina17.tgbot.utils.tgclient.TgClient;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // start the Telegram client
        TgClient.startClient();

        // Initialize drive service
        DriveService.initializeDriveService();

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new DhinaBot());
            LOGGER.info("Bot registered successfully");
        } catch (TelegramApiException e) {
            LOGGER.error("Bot registration failed", e);
        }
    }
}
