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

public class GdriveConfig {

    /**
     * Set true if you want to use Team drive
     */
    public static final Boolean USE_TEAM_DRIVE = Boolean.parseBoolean(
                                                                        System.getenv("USE_TEAM_DRIVE")
                                                                    );

    /*
     * Place your TeamDrive ID here
     */

    public static final String TEAM_DRIVE_ID = System.getenv("TEAM_DRIVE_ID");

    /*
     * Place your G-Index link here
     */
    public static final String GDRIVE_INDEX_LINK = System.getenv("GDRIVE_INDEX_LINK");

}
