package com.guerr.attendancecontrol.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final Locale LOCALE_ES = new Locale("es", "CL");

    public static String getTodayIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(new Date());
    }

    public static String formatDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "--";
        try {
            // Manejar formato yyyy-MM-dd
            String clean = dateStr.length() > 10 ? dateStr.substring(0, 10) : dateStr;
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date parsed = input.parse(clean);
            if (parsed != null) {
                SimpleDateFormat output = new SimpleDateFormat("EEEE, d 'de' MMM", LOCALE_ES);
                String formatted = output.format(parsed);
                // Primera letra mayúscula
                return formatted.substring(0, 1).toUpperCase(LOCALE_ES) + formatted.substring(1);
            }
        } catch (Exception ignored) {}
        return dateStr;
    }

    public static String formatTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return "--:--";
        try {
            if (timeStr.matches("^\\d{2}:\\d{2}(:\\d{2})?$")) {
                return timeStr.substring(0, 5);
            }
            if (timeStr.contains("T")) {
                String timePart = timeStr.substring(timeStr.indexOf("T") + 1);
                if (timePart.length() >= 5) {
                    return timePart.substring(0, 5);
                }
            }
        } catch (Exception ignored) {}
        return timeStr;
    }
}
