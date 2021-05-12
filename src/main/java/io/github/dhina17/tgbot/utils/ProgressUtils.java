/* DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
    
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

package io.github.dhina17.tgbot.utils;

public class ProgressUtils {
    public static String getPercentValue(Long totalSize, int percent){
        Long percentValueInBytes = (totalSize * percent) / 100;
        return getSizeinMB(percentValueInBytes);
    }

    public static String getSizeinMB(Long sizeInBytes){
        return String.format("%.2f", sizeInBytes * Math.pow(10, -6));
    }

    public static int getPercent(Long size, Long totalSize){
        return (int)((size * 100) / totalSize);
    }

    /**
     * If Character '%' presents in filename, it will cause Exception 1101 in index link
     * becuase of URL encoding.
     * since we are just appending filename with the index link.
     * '%' char should be encoded as '%25' in URL.
     * Instead of encode the chars in URL, just replace the chars.
     * @param name
     * @return
     */
    public static String correctFileName(String name) {
        if (name.contains("%")) {
            return name.replace("%", "_");
        }
        return name;
    }
}
