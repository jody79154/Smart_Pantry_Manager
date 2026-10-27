package com.example.smartpantry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.smartpantry.util.ExpiryHelper.Status;

import org.junit.Test;

import java.time.LocalDate;

public class ExpiryHelperTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 17);

    @Test
    public void missingOrInvalidDatesHaveNoStatus() {
        assertEquals(Status.NONE, ExpiryHelper.statusOf(null, TODAY, 3));
        assertEquals(Status.NONE, ExpiryHelper.statusOf("", TODAY, 3));
        assertEquals(Status.NONE, ExpiryHelper.statusOf("   ", TODAY, 3));
        assertEquals(Status.NONE, ExpiryHelper.statusOf("not a date", TODAY, 3));
        assertEquals(Status.NONE, ExpiryHelper.statusOf("2026-02-30", TODAY, 3));
        assertNull(ExpiryHelper.daysUntil(null, TODAY));
        assertNull(ExpiryHelper.daysUntil("garbage", TODAY));
    }

    @Test
    public void daysUntilCountsCalendarDays() {
        assertEquals(Integer.valueOf(0), ExpiryHelper.daysUntil("2026-10-17", TODAY));
        assertEquals(Integer.valueOf(1), ExpiryHelper.daysUntil("2026-10-18", TODAY));
        assertEquals(Integer.valueOf(-1), ExpiryHelper.daysUntil("2026-10-16", TODAY));
        assertEquals(Integer.valueOf(14), ExpiryHelper.daysUntil("2026-10-31", TODAY));
        assertEquals(Integer.valueOf(15), ExpiryHelper.daysUntil(" 2026-11-01 ", TODAY));
    }

    @Test
    public void daysUntilHandlesMonthYearAndLeapBoundaries() {
        assertEquals(Integer.valueOf(1), ExpiryHelper.daysUntil("2028-02-29", LocalDate.of(2028, 2, 28)));
        assertEquals(Integer.valueOf(1), ExpiryHelper.daysUntil("2028-03-01", LocalDate.of(2028, 2, 29)));
        assertEquals(Integer.valueOf(1), ExpiryHelper.daysUntil("2027-01-01", LocalDate.of(2026, 12, 31)));
    }

    @Test
    public void isExpiredIsTrueOnlyForDatesBeforeToday() {
        assertEquals(true, ExpiryHelper.isExpired("2026-10-16", TODAY));
        assertEquals(false, ExpiryHelper.isExpired("2026-10-17", TODAY));
        assertEquals(false, ExpiryHelper.isExpired("2026-10-18", TODAY));
        assertEquals(false, ExpiryHelper.isExpired(null, TODAY));
        assertEquals(false, ExpiryHelper.isExpired("", TODAY));
        assertEquals(false, ExpiryHelper.isExpired("garbage", TODAY));
    }

    @Test
    public void pastDatesAreExpired() {
        assertEquals(Status.EXPIRED, ExpiryHelper.statusOf("2026-10-16", TODAY, 3));
        assertEquals(Status.EXPIRED, ExpiryHelper.statusOf("2020-01-01", TODAY, 7));
    }

    @Test
    public void todayIsExpiringSoonNotExpired() {
        assertEquals(Status.EXPIRING_SOON, ExpiryHelper.statusOf("2026-10-17", TODAY, 3));
    }

    @Test
    public void warningWindowIncludesItsLastDay() {
        assertEquals(Status.EXPIRING_SOON, ExpiryHelper.statusOf("2026-10-20", TODAY, 3));
        assertEquals(Status.FRESH, ExpiryHelper.statusOf("2026-10-21", TODAY, 3));
        assertEquals(Status.EXPIRING_SOON, ExpiryHelper.statusOf("2026-10-22", TODAY, 5));
        assertEquals(Status.FRESH, ExpiryHelper.statusOf("2026-10-23", TODAY, 5));
        assertEquals(Status.EXPIRING_SOON, ExpiryHelper.statusOf("2026-10-24", TODAY, 7));
        assertEquals(Status.FRESH, ExpiryHelper.statusOf("2026-10-25", TODAY, 7));
    }

    @Test
    public void farFutureDatesAreFresh() {
        assertEquals(Status.FRESH, ExpiryHelper.statusOf("2027-10-17", TODAY, 7));
    }

    @Test
    public void onlyTheThreeOfferedWindowsAreAccepted() {
        assertEquals(3, ExpiryHelper.sanitizeWarningDays(3));
        assertEquals(5, ExpiryHelper.sanitizeWarningDays(5));
        assertEquals(7, ExpiryHelper.sanitizeWarningDays(7));
        assertEquals(ExpiryHelper.DEFAULT_WARNING_DAYS, ExpiryHelper.sanitizeWarningDays(0));
        assertEquals(ExpiryHelper.DEFAULT_WARNING_DAYS, ExpiryHelper.sanitizeWarningDays(4));
        assertEquals(ExpiryHelper.DEFAULT_WARNING_DAYS, ExpiryHelper.sanitizeWarningDays(-5));
        assertEquals(ExpiryHelper.DEFAULT_WARNING_DAYS, ExpiryHelper.sanitizeWarningDays(100));
    }
}
