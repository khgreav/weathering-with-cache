package com.github.khgreav.weatheringwithcache.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class DateTimeUtils {

    private DateTimeUtils() {
        throw new AssertionError("Cannot instantiate.");
    }

    public static long getTodayLastSecondEpoch(String timeZone) {
        ZoneId zone = ZoneId.of(timeZone);
        return ZonedDateTime.now(zone)
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay(zone)
            .minusSeconds(1)
            .toEpochSecond();
    }
    
}
