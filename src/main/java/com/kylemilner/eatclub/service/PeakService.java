package com.kylemilner.eatclub.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PeakService {

    private static final int MINUTES_PER_DAY = 1440;

    private final EatClubClient eatClubClient;
    private final EffectiveWindowResolver effectiveWindowResolver;

    public PeakService(EatClubClient eatClubClient,
            EffectiveWindowResolver effectiveWindowResolver) {
        this.eatClubClient = eatClubClient;
        this.effectiveWindowResolver = effectiveWindowResolver;
    }

    public TimeRange findPeakWindow() {

        List<Restaurant> restaurants = eatClubClient.getRestaurants();
        TimeRange peak = computePeakWindow(null, restaurants);

        return peak;
    }

    private TimeRange computePeakWindow(Duration fixedPeakDuration, List<Restaurant> restaurants) {
        var activeDealWindows = extractEffectiveDealWindows(restaurants);
        if (activeDealWindows.isEmpty()) {
            return null;
        }
        log.info("Found {} active deal windows for use in Peak calculation. {}", activeDealWindows.size(),
                activeDealWindows);

        int[] dealActivationMarkers = buildDealActivationMarkers(activeDealWindows);
        int[] dealsActiveAtMinute = prefixSumActiveDeals(dealActivationMarkers);
        int maxConcurrency = Arrays.stream(dealsActiveAtMinute).max().orElse(0);

        if (maxConcurrency == 0) {
            return null;
        }
        TimeRange peak = locateDynamicLengthPeak(dealsActiveAtMinute, maxConcurrency);

        log.info("Dynamic peak window: start={} end={} concurrentDeals={}",
                peak.start(), peak.end(), maxConcurrency);

        return peak;
    }

    // Creates an array marking the minutes of the day that deals activate or
    // deactivate
    private int[] buildDealActivationMarkers(List<TimeRange> activeDealWindows) {
        int[] dealActivationMarkers = new int[MINUTES_PER_DAY + 1];
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
                dealActivationMarkers[MINUTES_PER_DAY] -= 1;
                dealActivationMarkers[0] += 1;
                dealActivationMarkers[end] -= 1;
            }
        }
        return dealActivationMarkers;
    }

    // Computes a prefix sum to show how many deals are active at each minute of the
    // day.
    private int[] prefixSumActiveDeals(int[] dealActivationMarkers) {
        int[] dealsActiveAtMinute = new int[MINUTES_PER_DAY];
        int runningTotal = 0;
        // iterate **through** index 1440 so the wrap‑around balancing entry is included
        for (int i = 0; i <= MINUTES_PER_DAY; i++) {
            runningTotal += dealActivationMarkers[i];
            // only record counts for real minutes 0‑1439
            if (i < MINUTES_PER_DAY) {
                dealsActiveAtMinute[i] = runningTotal;
            }
        }
        return dealsActiveAtMinute;
    }

    // Finds the first (if multiple) peak range with maximum concurrent deals active
    private TimeRange locateDynamicLengthPeak(int[] dealsActiveAtMinute, int concurrencyTarget) {

        // Find the peak start
        int peakStartMinute = 0;
        while (dealsActiveAtMinute[peakStartMinute] != concurrencyTarget) {
            peakStartMinute++;
        }

        // Walk until peak concurrency ends, wrap over midnight
        int lengthMinutes = 0;
        while (lengthMinutes < MINUTES_PER_DAY &&
                dealsActiveAtMinute[(peakStartMinute + lengthMinutes) % MINUTES_PER_DAY] == concurrencyTarget) {
            lengthMinutes++;
        }

        int peakEndMinute = (peakStartMinute + lengthMinutes) % MINUTES_PER_DAY;

        LocalTime peakStart = LocalTime.of(peakStartMinute / 60, peakStartMinute % 60);
        LocalTime peakEnd = LocalTime.of(peakEndMinute / 60, peakEndMinute % 60);
        return new TimeRange(peakStart, peakEnd);
    }

    private List<TimeRange> extractEffectiveDealWindows(List<Restaurant> restaurants) {
        return restaurants.stream()
                .flatMap(r -> r.deals().stream()
                        .map(d -> effectiveWindowResolver.calculateDealEffectiveTime(d, r.operatingHours())))
                .filter(Objects::nonNull)
                .toList();
    }
}
