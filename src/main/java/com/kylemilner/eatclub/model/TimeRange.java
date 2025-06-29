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
     * Returns the portion of {@code a} that lies inside {@code b}.
     * <p>
     * If {@code a} starts before {@code b} it is clamped to
     * {@code b.start}. Likewise, if {@code a} ends after {@code b}
     * it is clamped to {@code b.end}. The result therefore never
     * extends outside {@code b}. If the two ranges do not overlap
     * at all this method returns {@code null}.
     */
    public static TimeRange intersection(TimeRange a, TimeRange b) {
        // Fast‑fail when there is no overlap at all
        if (!overlaps(a, b)) {
            return null;
        }

        // Clamp A’s bounds to B
        LocalTime start = b.contains(a.start) ? a.start : b.start;
        LocalTime end = b.contains(a.end) ? a.end : b.end;

        return new TimeRange(start, end);
    }

    private static boolean overlaps(TimeRange a, TimeRange b) {
        TimeRangeSegment[] segmentsA = toSegments(a);
        TimeRangeSegment[] segmentsB = toSegments(b);

        for (TimeRangeSegment sa : segmentsA) {
            for (TimeRangeSegment sb : segmentsB) {
                if (sa.end > sb.start && sb.end > sa.start) {
                    return true;
                }
            }
        }
        return false;
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
