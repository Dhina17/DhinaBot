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

                Client.client.send(new TdApi.SetTdlibParameters(parameters), authorizationRequestHandler);
                break;

            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                Client.client.send(new TdApi.CheckDatabaseEncryptionKey(), authorizationRequestHandler);
                break;

            case AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                System.out.println("Enter your phone number:");
                String phoneNumber = getInput();
                Client.client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null),
                        authorizationRequestHandler);
                break;
            }

            case AuthorizationStateWaitCode.CONSTRUCTOR: {
                System.out.println("Enter Authencation code:");
                String code = getInput();
                Client.client.send(new TdApi.CheckAuthenticationCode(code), authorizationRequestHandler);
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
                    LOGGER.error("Receive an error",object);
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
            LOGGER.error("Failed to read",e);
        }
        return str;
    }

}
