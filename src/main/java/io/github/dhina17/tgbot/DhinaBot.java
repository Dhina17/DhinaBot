//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot;

import org.telegram.abilitybots.api.bot.AbilityBot;

import io.github.dhina17.tgbot.abilities.Alive;
import io.github.dhina17.tgbot.abilities.DriveMirror;
import io.github.dhina17.tgbot.abilities.Help;
import io.github.dhina17.tgbot.abilities.Source;
import io.github.dhina17.tgbot.configs.BotConfig;

public class DhinaBot extends AbilityBot {
	protected DhinaBot() {
		super(BotConfig.BOT_TOKEN, BotConfig.BOT_USERNAME);
        addExtensions(new Alive(this),
                            new DriveMirror(this),
                            new Help(this),
                            new Source(this));
	}

	@Override
  	public long creatorId() {
    	return BotConfig.CREATOR_ID;
  	}

}
