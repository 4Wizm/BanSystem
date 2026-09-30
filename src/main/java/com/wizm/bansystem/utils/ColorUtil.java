package com.wizm.bansystem.utils;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}