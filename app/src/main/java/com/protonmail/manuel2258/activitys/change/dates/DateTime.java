/*
 * Date: 8/2/19 11:24 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

/*
 * Date: 8/1/19 12:27 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.dates;

import java.util.Objects;

/**
 * A simple container class for a date in a week
 */
public class DateTime {
    private final int hour;
    private final int minute;

    public DateTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTime dateTime = (DateTime) o;
        return hour == dateTime.hour &&
                minute == dateTime.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }
}
