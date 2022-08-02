//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.configs;

public class TDLibConfig {

    /**
     * tdlib directory.Change it to your favourable location.
     */
    public static String TDLIB_DATABASE_DIRECTORY = "tdlib";

    /**
     * Put your Telegram API ID here
     */
    public static int API_ID = Integer.parseInt(
                                            System.getenv("API_ID")
                                        );

    /**
     * Put your Telegram API HASH here
     */
    public static String API_HASH = System.getenv("API_HASH");

    /**
     * DeviceName
     */
    public static String DEVICE_NAME = "DhinaBot";

    /**
     * Application version
     */
    public static String APP_VERSION = "1.0";
    
}
