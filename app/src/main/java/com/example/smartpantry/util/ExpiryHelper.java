package com.example.smartpantry.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public final class ExpiryHelper {

    public enum Status {
        NONE, FRESH, EXPIRING_SOON, EXPIRED
    }

    public static final int[] WARNING_DAY_OPTIONS = {3, 5, 7};
    public static final int DEFAULT_WARNING_DAYS = 3;

    private ExpiryHelper() {
    }

    public static Integer daysUntil(String isoDate, LocalDate today) {
        if (isoDate == null || isoDate.trim().isEmpty()) {
            return null;
        }
        try {
            return (int) ChronoUnit.DAYS.between(today, LocalDate.parse(isoDate.trim()));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Status statusOf(String isoDate, LocalDate today, int warningDays) {
        Integer days = daysUntil(isoDate, today);
        if (days == null) {
            return Status.NONE;
        }
        if (days < 0) {
            return Status.EXPIRED;
        }
        return days <= warningDays ? Status.EXPIRING_SOON : Status.FRESH;
    }

    public static int sanitizeWarningDays(int days) {
        for (int option : WARNING_DAY_OPTIONS) {
            if (option == days) {
                return days;
            }
        }
        return DEFAULT_WARNING_DAYS;
    }
}
