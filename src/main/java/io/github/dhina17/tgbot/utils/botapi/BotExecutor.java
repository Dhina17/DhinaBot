//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils.botapi;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotExecutor {
    private AbilityBot bot;
    private static final Logger LOGGER = LoggerFactory.getLogger(BotExecutor.class);
    private Queue<Object> messages;
    private Timer timer;

    public BotExecutor(AbilityBot bot, MessageQueue msgeQueue){
        this.bot = bot;
        this.messages = msgeQueue.getQueue();
    }

    public void start(){
        executor();
    }

    public void executor(){
        timer = new Timer();
        TimerTask task = new TimerTask(){

            @Override
            public void run() {
                Object peek = messages.peek();
                if(peek != null){
                    Object obj = messages.poll();
                    if(obj instanceof EditMessageText){
                        execute((EditMessageText) obj);
                    }else if(obj instanceof SendMessage){
                        execute((SendMessage) obj);
                    }else if(obj instanceof DeleteMessage) {
                        execute((DeleteMessage) obj);
                    }else if(obj instanceof String){
                        stop(); // Stop the timer thread when String object;
                    }
                }
            }

        };
        timer.scheduleAtFixedRate(task, 0, 3160); // 60 / 19 = 3.16 (approx). Run 19 times/minute

    }

    public void execute(EditMessageText editMsge){

        try {
            bot.execute(editMsge);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to execute the method",e);
        }

    }

    public void execute(SendMessage msge){
        try {
            bot.execute(msge);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to execute the method",e);
        }
    }

    public void execute(DeleteMessage dmsge){
        try {
            bot.execute(dmsge);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to execute the method",e);
        }
    }

    public void stop(){
        timer.cancel();
    }
}
