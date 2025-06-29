package com.kylemilner.eatclub.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class TimeUtil {

    private TimeUtil() {
        // Prevent instantiation
    }

    private static final DateTimeFormatter AM_PM_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h:mma")
            .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter QUERY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /* Parse time in "3:00pm" format to LocalTime */
    public static LocalTime parseAmPmTime(String amPmFormatTimeString) {
        return LocalTime.parse(amPmFormatTimeString.replace(" ", ""), AM_PM_FORMATTER);
    }

    /**
     * Parse query parameter in HH:mm format to LocalTime, or null if invalid time
     * 
     * @returns LocalTime or null if parsing fails
     */
    public static LocalTime parseQueryParam(String hhmm) {
        try {
            return LocalTime.parse(hhmm, QUERY_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /* Format LocalTime to "3:00pm" format */
    public static String formatToAmPmTime(LocalTime t) {
        return t.format(AM_PM_FORMATTER).toLowerCase();
    }
}
