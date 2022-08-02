//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.configs;

import java.util.Optional;

public class GdriveConfig {

    /**
     * Set true if you want to use Team drive (Optional)
     */
    public static final Boolean USE_TEAM_DRIVE = Optional.ofNullable(Boolean.parseBoolean(
                                                                        System.getenv("USE_TEAM_DRIVE")
                                                                    )).orElse(false);

    /*
     * Place your TeamDrive ID here (Optional)
     */

    public static final String TEAM_DRIVE_ID = System.getenv("TEAM_DRIVE_ID");

    /*
     * Place your G-Index link here
     */
    public static final String GDRIVE_INDEX_LINK = System.getenv("GDRIVE_INDEX_LINK");

}
