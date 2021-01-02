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

package io.github.dhina17.tgbot.utils.botapi;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public class MessageQueue {

    // Create a Queue of messages
    private final Queue<Object> messages = new ConcurrentLinkedQueue<>();

    private String chatId;
    private int messageId;

    private EditMessageText editMessage;

    public MessageQueue setChatId(String chatId){
        this.chatId = chatId;
        return this;
    }

    public MessageQueue setMessageId(int messageId){
        this.messageId = messageId;
        return this;
    }

    public void add(Object object){
        messages.offer(object);
    }

    public void addEdit(final String text){
        editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setParseMode(ParseMode.HTML);
        editMessage.setText(text);
        add(editMessage);
    }

    public Queue<Object> getQueue(){
        return messages;
    }
}
