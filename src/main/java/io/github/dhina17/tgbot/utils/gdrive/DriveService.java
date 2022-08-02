//
// DhinaBot - A simple telegram bot
// Copyright (C) 2022  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

package io.github.dhina17.tgbot.utils.gdrive;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.Drive;

public class DriveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriveService.class);

    protected static Drive driveService;

    protected static Drive getService() {
        return driveService;
    }

    public static synchronized void initializeDriveService() {
        if (driveService == null) {
            try {
                driveService = OAuth.getDriveService();
                LOGGER.info("OAuth authentication completed successfully");
            } catch (IOException | GeneralSecurityException e) {
                String error = "OAuth failed";
                if (e instanceof FileNotFoundException) {
                    error += " - credentials.json is not found in the current dir";
                }
                LOGGER.error(error, e);
            }
        }
    }

}
