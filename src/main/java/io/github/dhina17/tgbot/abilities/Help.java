/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2020  Dhina17 <dhinalogu@gmail.com>
    
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
                            + "1) /paste - Reply to a text or document(text mime) to get dogbin link\n\n"
                            + "2) /getpaste  - Reply to a dogbin link to get its content\n\n"
                            + "3) /mirror - Usage : /mirror <link>  or Reply to a file to mirror\n\n"
                            + "4) /help - To get a help(this message)";

                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setReplyToMessageId(commandMessageId);
                    message.setText(helpMessage);

                    try {
                        bot.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    })
                    .build();
    }
    
}
