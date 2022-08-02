//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
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

public class Alive implements AbilityExtension{
    
    private AbilityBot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(Alive.class);
    public Alive(AbilityBot bot) {
        this.bot = bot;
    }

    /**
     *  Ability - Alive - To check whether the bot is alive or not
     */

    public Ability alive(){
        return Ability
        .builder()
        .name("alive")
        .info("To check whether bot is alive or not")
        .locality(Locality.GROUP)
        .privacy(Privacy.ADMIN)
        .action(consumer -> {
            Long chatId = consumer.chatId();
            Update upd = consumer.update();
            Message commandMessage = upd.getMessage();
            int commandMessageId = commandMessage.getMessageId();

            final String aliveText = "Hey, I will be alive as long as Dhina loves me...\n\nJust hit /help.";

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setReplyToMessageId(commandMessageId);
            message.setText(aliveText);

            try {
                bot.execute(message);
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to execute the method",e);
            }
            })
            .build();
    }
}
