/*
 * Date: 8/2/19 11:24 AM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

/*
 * Date: 8/1/19 3:42 PM
 * Author: Manuel Schmidbauer, manuel2258@protonmail.com
 * Project: lighthub_client
 */

package com.protonmail.manuel2258.activitys.change.dates;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A container for several same DateRanges in different days
 */
public class RangeContainer implements Comparable<RangeContainer> {

    /** Simple reference to the first item */
    private DateRange dateRange;

    /** A List of all contained days */
    private final Set<Integer> days = new HashSet<>();

    public RangeContainer(DateRange startRange, int startDay) {
        this.dateRange = startRange;
        days.add(startDay);
    }

    public RangeContainer(RangeContainer that) {
        this.dateRange = new DateRange(that.getDateRange());
        days.addAll(that.getDays());
    }

    public DateTime getStart() {
        return dateRange.getStart();
    }

    public DateTime getEnd() {
        return dateRange.getEnd();
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public Set<Integer> getDays() {
        return days;
    }

    public void setDateRangeFromString(String startDate, String endDate) throws IllegalArgumentException {
        try {
            this.dateRange = new DateRange(getTimeFromString(startDate),
                    getTimeFromString(endDate));
        } catch (NumberFormatException ignored) {}
    }

    public boolean addDay(int day) {
        return days.add(day);
    }

    public boolean removeDay(int day) {
        return days.remove(day);
    }

    /**
     * Checks if other is containable in this container, if so adds it
     * @param other The to add DateRange
     * @param day The to add at day
     * @return True if added, false otherwise
     */
    public boolean addIfContained(DateRange other, int day) {
        final boolean added;
        added = other.equals(dateRange);

        if(added) {
            days.add(day);
        }
        return added;
    }

    /** Returns a json Array of the contained DateRanges
     * @return A String of a jsonArray representing the to send data
     */
    public String getFullJsonString() throws IllegalStateException {
        if(days.size() <= 0)
            throw new IllegalStateException();
        JSONArray dataJson = new JSONArray();
        for(Integer day: days) {
            try {
                dataJson.put(new JSONObject(dateRange.getJsonStringForDay(day)));
            } catch (JSONException jsonException) {
                throw new RuntimeException();
            }
        }
        return dataJson.toString();
    }

    public Collection<Integer> getReadonlyDays() {
        return Collections.unmodifiableCollection(days);
    }

    public float[] getAverageTime() {
        float[] averages = new float[2];
        averages[0] = (getStart().getHour() + getEnd().getHour()) / 2f;
        averages[1] = (getStart().getMinute() + getEnd().getMinute()) / 2f;
        return averages;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        DateTime start = dateRange.getStart();
        DateTime end = dateRange.getEnd();
        return String.format("%d:%d-%d:%d",
                start.getHour(), start.getMinute(), end.getHour(), end.getMinute());
    }

    @Override
    public int compareTo(RangeContainer that) {
        float[] thisAverages = getAverageTime();
        float[] thatAverages = that.getAverageTime();
        int hourDif = (int)Math.signum(thatAverages[0]-thatAverages[0]);
        if(hourDif != 0) {
            return hourDif;
        }
        return (int)Math.signum(thatAverages[1]-thatAverages[1]);
    }

    private DateTime getTimeFromString(String date) throws IllegalArgumentException, NumberFormatException {
        int firstBinding = date.indexOf(':');
        if(firstBinding != date.lastIndexOf(':')) {
            throw new IllegalArgumentException();
        }
        int hour = Integer.parseInt(date.substring(0, firstBinding));
        int minute = Integer.parseInt(date.substring(firstBinding + 1, date.length()));
        return new DateTime(hour, minute);
    }
}
