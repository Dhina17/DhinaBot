//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.abilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Help implements AbilityExtension {

    private AbilityBot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(Help.class);

    public Help(AbilityBot bot) {
        this.bot = bot;
    }

    /**
     *  Ability - help - To get available commands and its usage
     */

    public Ability help() {
        return Ability
                .builder()
                .name("help")
                .info("Get available commands")
                .locality(Locality.GROUP)
                .privacy(Privacy.ADMIN)
                .action(consumer -> {
                    Long chatId = consumer.chatId();
                    Update upd = consumer.update();
                    Message commandMessage = upd.getMessage();
                    int commandMessageId = commandMessage.getMessageId();

                    final String helpMessage = "Available commands:\n\n"
                            + "1) /alive - Just to check whether I m alive or not.\n\n"
                            + "2) /mirror - Usage : /mirror <link>  or Reply to a file to mirror.\n\n"
                            + "3) /source - To get the source of this bot.\n\n"
                            + "4) /help - To get a help(this message).";

                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setReplyToMessageId(commandMessageId);
                    message.setText(helpMessage);

                    try {
                        bot.execute(message);
                    } catch (TelegramApiException e) {
                        LOGGER.error("Failed to execute the method",e);
                    }
                    })
                    .build();
    }
    
}
