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

package io.github.dhina17.tgbot;

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
