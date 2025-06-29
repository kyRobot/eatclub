package com.kylemilner.eatclub.model;

import java.time.LocalTime;

public record TimeRange(LocalTime start, LocalTime end) {

    private static final int MINUTES_PER_DAY = 1440;

    /** Represents a half‑open interval [start, end) in minutes‑of‑day. */
    private record TimeRangeSegment(int start, int end) {
    }

    public boolean wrapsAroundMidnight() {
        return start.isAfter(end);
    }

    public boolean withinSameDay() {
        return !wrapsAroundMidnight();
    }

    public boolean contains(LocalTime t) {
        if (start.isBefore(end)) {
            return (t.equals(start) || t.isAfter(start)) && t.isBefore(end);
        } else {
            // crosses midnight
            return (t.equals(start) || t.isAfter(start)) || t.isBefore(end);
        }
    }

    /**
     * Returns the overlap of two TimeRanges, handling cases where
     * one or both ranges wrap past midnight (e.g. 22:00‑02:00).
     * If no overlap exists, returns {@code null}.
     */
    public static TimeRange intersection(TimeRange a, TimeRange b) {
        // Convert each range into 1 or 2 non‑wrapping segment
        TimeRangeSegment[] segmentsA = toSegments(a);
        TimeRangeSegment[] segmentsB = toSegments(b);

        for (TimeRangeSegment sa : segmentsA) {
            for (TimeRangeSegment sb : segmentsB) {
                int s = Math.max(sa.start, sb.start);
                int e = Math.min(sa.end, sb.end);
                if (e > s) {
                    LocalTime start = LocalTime.MIN.plusMinutes(s);
                    LocalTime end = LocalTime.MIN.plusMinutes(e % MINUTES_PER_DAY);
                    return new TimeRange(start, end);
                }
            }
        }
        return null; // no overlap
    }

    /**
     * Splits a possibly wrapping TimeRange into 1 (normal) or 2
     * (wrap‑around) segments measured in minutes since 00:00.
     */
    private static TimeRangeSegment[] toSegments(TimeRange tr) {
        int startMinute = tr.start.toSecondOfDay() / 60;
        int endMinute = tr.end.toSecondOfDay() / 60;

        if (tr.withinSameDay()) {
            return new TimeRangeSegment[] { new TimeRangeSegment(startMinute, endMinute) };
        }
        return new TimeRangeSegment[] {
                new TimeRangeSegment(startMinute, MINUTES_PER_DAY),
                new TimeRangeSegment(0, endMinute)
        };
    }
}
