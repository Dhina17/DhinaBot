//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.configs;

public class BotConfig {
    /*
       Put your Bot token here
    */
    public static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    /*
       Put your Bot username here
    */
    public static final String BOT_USERNAME = System.getenv("BOT_USERNAME");

    /*
       Put your telegram user id here
    */
    public static final int CREATOR_ID = Integer.parseInt(
                                                         System.getenv("CREATOR_ID")
                                                      );
    
}
