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

package io.github.dhina17.tgbot.abilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Source implements AbilityExtension{
    
    private AbilityBot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(Source.class);

    public Source(AbilityBot bot) {
        this.bot = bot;
    }

    /**
     *  Ability - Source - To get the source of this bot
     */

    public Ability getsource(){
        return Ability
        .builder()
        .name("source")
        .info("To get the source of the bot")
        .locality(Locality.ALL) // Everywhere
        .privacy(Privacy.PUBLIC) // Open for all
        .action(consumer -> {
            Long chatId = consumer.chatId();
            Update upd = consumer.update();
            Message commandMessage = upd.getMessage();
            int commandMessageId = commandMessage.getMessageId();

            final String text = "Source for this bot can be found here..\n<b>Github:</b> https://github.com/Dhina17/DhinaBot";

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setReplyToMessageId(commandMessageId);
            message.setParseMode(ParseMode.HTML);
            message.setDisableWebPagePreview(true);
            message.setText(text);

            try {
                bot.execute(message);
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to execute the method",e);
            }
            })
            .build();
    }
}
