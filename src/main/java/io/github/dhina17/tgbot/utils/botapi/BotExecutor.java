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
import java.util.Timer;
import java.util.TimerTask;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotExecutor {
    private AbilityBot bot;
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
            e.printStackTrace();
        }

    }

    public void execute(SendMessage msge){
        try {
            bot.execute(msge);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void execute(DeleteMessage dmsge){
        try {
            bot.execute(dmsge);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        timer.cancel();
    }
}
