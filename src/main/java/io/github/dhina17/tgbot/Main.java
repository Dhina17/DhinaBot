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

package io.github.dhina17.tgbot;

import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import io.github.dhina17.tgbot.providers.Provider;
import io.github.dhina17.tgbot.utils.tgclient.AuthorizationUpdate;
import io.github.dhina17.tgbot.utils.tgclient.Client;
import io.github.dhina17.tgbot.utils.tgclient.TgClientUtils;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import it.tdlight.tdlight.ClientManager;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        CompletableFuture<Void> tdlib = CompletableFuture.runAsync(() -> {
            try {
                // Initialize the TDlib
                Init.start();
                Client.client = ClientManager.create();
                Client.client.initialize(TgClientUtils.updateHandler, null, null);
                Client.client.execute(new TdApi.SetLogVerbosityLevel(0));
                if (Client.client.execute(new TdApi.SetLogStream(
                        new TdApi.LogStreamFile("tdlib.log", 1 << 27, false))) instanceof TdApi.Error) {
                    throw new IOError(new IOException("Write access to the current directory is required"));
                }
                AuthorizationUpdate.authorizationLock.lock();
                try {
                    while (!AuthorizationUpdate.haveAuthorization) {
                        AuthorizationUpdate.gotAuthorization.await();
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Authorization Interrupted", e);
                } finally {
                    AuthorizationUpdate.authorizationLock.unlock();
                }
            } catch (CantLoadLibrary e1) {
                LOGGER.error("Failed to load library", e1);
            }

        });
        tdlib.join();

        // Initialize drive service
        try {
            Provider.initializeDriveService();
            LOGGER.info("OAuth authentication completed successfully");
        } catch (IOException | GeneralSecurityException e1) {
            String error = "OAuth failed";
            if (e1 instanceof FileNotFoundException) {
                error += " - credentials.json is not found in the current dir";
            }
            LOGGER.error(error, e1);
        }

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new DhinaBot());
            LOGGER.info("Bot registered successfully");
        } catch (TelegramApiException e) {
            LOGGER.error("Bot registration failed", e);
        }
    }
}
