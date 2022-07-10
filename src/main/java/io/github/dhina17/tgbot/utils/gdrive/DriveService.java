/*  DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2022  Dhina17 <dhinalogu@gmail.com>

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
