//
// DhinaBot - A simple telegram bot
// Copyright (C) 2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils.tgclient;

import java.io.IOError;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tdlight.common.Init;
import it.tdlight.common.TelegramClient;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import it.tdlight.tdlight.ClientManager;

public class TgClient {

    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(TgClient.class);

    // Telegram client
    private static TelegramClient client;

    protected static TelegramClient getClient() {
        return client;
    }

    public static synchronized void startClient() {
        if (client == null) {
            initializeClient();
        }
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
