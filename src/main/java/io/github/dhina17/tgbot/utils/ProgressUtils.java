//
// DhinaBot - A simple telegram bot
// Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later
//

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
