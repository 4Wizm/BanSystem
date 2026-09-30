package com.wizm.bansystem.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    private static final TimeZone TZ = TimeZone.getTimeZone("Europe/Rome");

    public static long parseDuration(String s) {
        if (s == null || s.isEmpty()) return -1;
        s = s.trim().toLowerCase();

        if (s.matches("\\d+")) return -1;

        char unit = s.charAt(s.length() - 1);
        String numStr = s.substring(0, s.length() - 1);
        long value;
        try {
            value = Long.parseLong(numStr);
        } catch (NumberFormatException e) {
            return -1;
        }

        switch (unit) {
            case 's': return value * 1000L;
            case 'm': return value * 60_000L;
            case 'h': return value * 3_600_000L;
            case 'd': return value * 86_400_000L;
            case 'w': return value * 604_800_000L;
            default:  return -1;
        }
    }

    public static String formatDuration(long ms) {
        if (ms < 0) return "Permanente";
        if (ms == 0) return "0s";

        long sec = ms / 1000;
        long d = sec / 86400; sec %= 86400;
        long h = sec / 3600;  sec %= 3600;
        long m = sec / 60;    sec %= 60;
        long s = sec;

        StringBuilder sb = new StringBuilder();
        if (d > 0) sb.append(d).append("d ");
        if (h > 0) sb.append(h).append("h ");
        if (m > 0) sb.append(m).append("m ");
        if (s > 0) sb.append(s).append("s");
        return sb.toString().trim();
    }

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sdf.setTimeZone(TZ);
        return sdf.format(new Date(timestamp));
    }
}