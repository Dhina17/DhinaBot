//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

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
    private String prevText = "";

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
        if(!prevText.equals(text)){
            editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setParseMode(ParseMode.HTML);
            editMessage.setText(text);
            add(editMessage);
            prevText = text;
        }

    }

    public Queue<Object> getQueue(){
        return messages;
    }
}
