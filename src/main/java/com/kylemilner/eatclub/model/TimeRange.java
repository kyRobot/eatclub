package com.kylemilner.eatclub.model;

import java.time.LocalTime;

public record TimeRange(LocalTime start, LocalTime end) {

    public boolean contains(LocalTime t) {
        if (start.isBefore(end)) {
            return (t.equals(start) || t.isAfter(start)) && t.isBefore(end);
        } else {
            // crosses midnight
            return (t.equals(start) || t.isAfter(start)) || t.isBefore(end);
        }
    }

    /**
     * Finds the intersection of two time ranges so that the result contains both,
     * if it exists
     * If the ranges do not overlap, return null.
     * 
     * @param a the first time range
     * @param b the second time range
     * @return a new TimeRange representing the intersection, or null no
     *         intersection exists
     */
    public static TimeRange intersection(TimeRange a, TimeRange b) {
        LocalTime earliestStart = a.start.isAfter(b.start) ? a.start : b.start;
        LocalTime latestEnd = a.end.isBefore(b.end) ? a.end : b.end;
        return latestEnd.isAfter(earliestStart) ? new TimeRange(earliestStart, latestEnd) : null;
    }
}
