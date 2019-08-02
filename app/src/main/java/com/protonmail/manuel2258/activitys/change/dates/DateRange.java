/*
 * Date: 8/2/19 11:24 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

/*
 * Date: 8/1/19 12:26 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.dates;

import android.annotation.SuppressLint;

import java.util.Objects;

/**
 * Simple container class for a range between two DateTimes
 */
public class DateRange {

    private final DateTime start;
    private final DateTime end;

    public DateRange(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    @SuppressLint("DefaultLocale")
    public String getJsonStringForDay(int day) {
        return String.format("{\"s_d\": %d, \"s_h\": %d, \"s_m\": %d," +
                "\"e_d\": %d, \"e_h\": %d, \"e_m\": %d}",
                day, start.getHour(), start.getMinute(),
                day, end.getHour(), end.getMinute());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(start, dateRange.start) &&
                Objects.equals(end, dateRange.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
