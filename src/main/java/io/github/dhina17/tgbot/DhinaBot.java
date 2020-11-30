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

import org.telegram.abilitybots.api.bot.AbilityBot;

import io.github.dhina17.tgbot.abilities.Dogbin;

public class DhinaBot extends AbilityBot {
	protected DhinaBot() {
		super(BotConfig.BOT_TOKEN, BotConfig.BOT_USERNAME);
		addExtensions(new Dogbin(this));
	}

	@Override
  	public int creatorId() {
    	return BotConfig.CREATOR_ID;
  	}

}
