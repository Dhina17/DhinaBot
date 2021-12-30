/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2021  Dhina17 <dhinalogu@gmail.com>
    
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

package io.github.dhina17.tgbot.providers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.drive.Drive;

import io.github.dhina17.tgbot.utils.gdrive.OAuth;

public class Provider {
    
    // Singleton Drive Service
    private static Drive driveService;
    
    public static synchronized void initializeDriveService() throws IOException, GeneralSecurityException {
        if (driveService == null) {
            driveService = OAuth.getDriveService();
        }
    }

    public static Drive getDriverService() {
        return driveService;
    }

}