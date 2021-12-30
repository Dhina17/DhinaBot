/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
    
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

package io.github.dhina17.tgbot.providers;

import java.io.IOError;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;

import com.google.api.services.drive.Drive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.utils.gdrive.OAuth;
import io.github.dhina17.tgbot.utils.tgclient.AuthorizationUpdate;
import io.github.dhina17.tgbot.utils.tgclient.TgClientUtils;
import it.tdlight.common.Init;
import it.tdlight.common.TelegramClient;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import it.tdlight.tdlight.ClientManager;

public class Provider {
    
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(Provider.class);

    // Singleton Drive Service
    private static Drive driveService;

    // Singleton Telegram Client
    private static TelegramClient client;
    
    public static synchronized void initializeDriveService() throws IOException, GeneralSecurityException {
        if (driveService == null) {
            driveService = OAuth.getDriveService();
        }
    }

    public static synchronized void initializeTgClient() {
        if (client == null) {
            initializeClient();
        }
    }

    public static Drive getDriverService() {
        return driveService;
    }

    public static TelegramClient getTgClient() {
        return client;
    }

    private static void initializeClient() {
        CompletableFuture.runAsync(() -> {
            try {
                // Initialize the TDlib
                Init.start();
                client = ClientManager.create();
                client.initialize(TgClientUtils.updateHandler, null, null);
                client.execute(new TdApi.SetLogVerbosityLevel(0));
                if (client.execute(new TdApi.SetLogStream(
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

        }).join();
    }

}