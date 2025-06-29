package com.kylemilner.eatclub.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PeakService {

    private final EatClubClient eatClubClient;
    private final EffectiveWindowResolver effectiveWindowResolver;
    private final int peakWindowSpanMinutes;

    public PeakService(EatClubClient eatClubClient, EffectiveWindowResolver effectiveWindowResolver,
            @Value("${peak.window.span.minutes:60}") int peakWindowSpanMinutes) {
        this.eatClubClient = eatClubClient;
        this.effectiveWindowResolver = effectiveWindowResolver;
        this.peakWindowSpanMinutes = peakWindowSpanMinutes;
    }

    public TimeRange findPeakWindow() {

        List<Restaurant> restaurants = eatClubClient.getRestaurants();
        TimeRange peak = computePeakWindow(Duration.ofMinutes(peakWindowSpanMinutes), restaurants);

        return peak;
    }

    private TimeRange computePeakWindow(Duration fixedPeakDuration, List<Restaurant> restaurants) {
        var activeDealWindows = extractEffectiveDealWindows(restaurants);
        if (activeDealWindows.isEmpty()) {
            return null;
        }
        log.info("Found {} active deal windows for use in Peak calculation", activeDealWindows.size());

        int minutesInDay = 1440;
        int[] dealActivationMarkers = buildDealActivationMarkers(activeDealWindows, minutesInDay);
        int[] dealsActiveAtMinute = prefixSumActiveDeals(dealActivationMarkers);

        int peakWindowDurationInMinutes = (int) fixedPeakDuration.toMinutes();
        // Only consider the first 1440 minutes (0-1439), ignore the sentinel at 1440
        int peakStartMinute = findPeakWindowStartWithSlidingWindow(dealsActiveAtMinute, peakWindowDurationInMinutes,
                minutesInDay);
        int peakEndMinute = (peakStartMinute + peakWindowDurationInMinutes) % minutesInDay; // handle midnight wrap

        log.info("Peak window found starting at minute {} ({}), ending at minute {} ({}), with duration {} minutes",
                peakStartMinute, LocalTime.of(peakStartMinute / 60, peakStartMinute % 60),
                peakEndMinute, LocalTime.of(peakEndMinute / 60, peakEndMinute % 60), peakWindowDurationInMinutes);

        LocalTime peakStart = LocalTime.of(peakStartMinute / 60, peakStartMinute % 60);
        LocalTime peakEnd = LocalTime.of(peakEndMinute / 60, peakEndMinute % 60);
        return new TimeRange(peakStart, peakEnd);
    }

    // Creates an array marking the minutes of the day that deals activate or
    // deactivate
    private int[] buildDealActivationMarkers(List<TimeRange> activeDealWindows, int minutesInDay) {
        int[] dealActivationMarkers = new int[minutesInDay + 1];
        for (TimeRange range : activeDealWindows) {
            int start = range.start().toSecondOfDay() / 60;
            int end = range.end().toSecondOfDay() / 60;
            if (range.withinSameDay()) {
                log.debug("Marking {} active from {} to {}", range, start, end);
                dealActivationMarkers[start] += 1;
                dealActivationMarkers[end] -= 1;
            } else {
                log.debug("Marking {} active from {} to {} and {} to {}", range, start, 1440, 0, end);
                dealActivationMarkers[start] += 1;
                dealActivationMarkers[minutesInDay] -= 1;
                dealActivationMarkers[0] += 1;
                dealActivationMarkers[end] -= 1;
            }
        }
        return dealActivationMarkers;
    }

    // Computes a prefix sum to show how many deals are active at each minute of the
    // day. Only returns the first 1440 minutes (0-1439), ignoring the sentinel at
    // 1440
    private int[] prefixSumActiveDeals(int[] dealActivationMarkers) {
        int minutesInDay = 1440;
        int[] dealsActiveAtMinute = new int[minutesInDay];
        int runningTotal = 0;
        for (int i = 0; i < minutesInDay; i++) {
            runningTotal += dealActivationMarkers[i];
            dealsActiveAtMinute[i] = runningTotal;
        }
        return dealsActiveAtMinute;
    }

    /**
     * Finds the start minute of the fixed‑size window that contains the largest
     * number of concurrently active deals. Uses a sliding window approach
     *
     * Earliest window wins on ties
     */
    private int findPeakWindowStartWithSlidingWindow(int[] dealsActiveInEachMinute, int windowSize, int minutesInDay) {
        int bestStart = 0;
        int bestMax = -1;

        for (int start = 0; start < minutesInDay; start++) {
            int windowMax = 0;
            for (int i = 0; i < windowSize; i++) {
                int idx = (start + i) % minutesInDay; // wrap around midnight
                windowMax = Math.max(windowMax, dealsActiveInEachMinute[idx]);
            }

            if (windowMax > bestMax) {
                bestMax = windowMax;
                bestStart = start;
            } else if (windowMax == bestMax) {
                // Tie-breaker: Prefer a window whose first minute is at the peak, such that the
                // window 'activates' at the peak hit time, not only if any minute of the window
                // is at a peak load
                boolean currentWindowStartsAtPeak = dealsActiveInEachMinute[start] == windowMax;
                boolean bestWindowStartsAtPeak = dealsActiveInEachMinute[bestStart] == bestMax;
                if (currentWindowStartsAtPeak && !bestWindowStartsAtPeak) {
                    bestStart = start;
                }
            }
        }
        return bestStart;
    }

    private List<TimeRange> extractEffectiveDealWindows(List<Restaurant> restaurants) {
        return restaurants.stream()
                .flatMap(r -> r.deals().stream()
                        .map(d -> effectiveWindowResolver.calculateDealEffectiveTime(d, r.operatingHours())))
                .filter(Objects::nonNull)
                .toList();
    }
}
