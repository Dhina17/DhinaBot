//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils.tgclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dhina17.tgbot.configs.TDLibConfig;
import it.tdlight.common.ResultHandler;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.AuthorizationState;
import it.tdlight.jni.TdApi.AuthorizationStateWaitCode;
import it.tdlight.jni.TdApi.AuthorizationStateWaitPhoneNumber;
import it.tdlight.jni.TdApi.AuthorizationStateWaitTdlibParameters;
import it.tdlight.jni.TdApi.TdlibParameters;

public class AuthorizationUpdate {

    private static AuthorizationState authorizationState = null;
    private static AuthorizationRequestHandler authorizationRequestHandler = new AuthorizationRequestHandler();

    public static volatile boolean haveAuthorization = false;
    public static final Lock authorizationLock = new ReentrantLock();
    public static final Condition gotAuthorization = authorizationLock.newCondition();
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationUpdate.class);

    public static void onAuthorizationStateUpdated(AuthorizationState authorizationState) {
        if (authorizationState != null) {
            AuthorizationUpdate.authorizationState = authorizationState;
        }
        switch (AuthorizationUpdate.authorizationState.getConstructor()) {
            case AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdlibParameters parameters = new TdlibParameters();
                parameters.databaseDirectory = TDLibConfig.TDLIB_DATABASE_DIRECTORY;
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = TDLibConfig.API_ID;
                parameters.apiHash = TDLibConfig.API_HASH;
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = TDLibConfig.DEVICE_NAME;
                parameters.applicationVersion = TDLibConfig.APP_VERSION;
                parameters.enableStorageOptimizer = true;

                TgClient.getClient().send(new TdApi.SetTdlibParameters(parameters), authorizationRequestHandler);
                break;

            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                TgClient.getClient().send(new TdApi.CheckDatabaseEncryptionKey(), authorizationRequestHandler);
                break;

            case AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                System.out.println("Enter your phone number:");
                String phoneNumber = getInput();
                TgClient.getClient().send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null),
                        authorizationRequestHandler);
                break;
            }

            case AuthorizationStateWaitCode.CONSTRUCTOR: {
                System.out.println("Enter Authencation code:");
                String code = getInput();
                TgClient.getClient().send(new TdApi.CheckAuthenticationCode(code), authorizationRequestHandler);
                break;
            }

            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                System.out.println("Enter your password:");
                String password = getInput();
                TgClient.getClient().send(new TdApi.CheckAuthenticationPassword(password),
                        authorizationRequestHandler);
                break;
            }

            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                break;
        }
    }

    private static class AuthorizationRequestHandler implements ResultHandler {

        @Override
        public void onResult(it.tdlight.jni.TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    LOGGER.error("Receive an error", object);
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    // result is already received through UpdateAuthorizationState, nothing to do
                    break;
                default:
                    LOGGER.error("Receive wrong response from TDLib", object);
            }
        }
    }

    public static String getInput() {
        String str = "";
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        try {
            str = read.readLine();
        } catch (IOException e) {
            LOGGER.error("Failed to read", e);
        }
        return str;
    }

}
