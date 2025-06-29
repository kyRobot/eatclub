package com.kylemilner.eatclub.service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

@Component
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

    private TimeRange computePeakWindow(Duration window, List<Restaurant> restaurants) {
        var timeRangesForDeals = extractEffectiveDealWindows(restaurants);
        return timeRangesForDeals.stream().findFirst().orElse(null);
    }

    private List<TimeRange> extractEffectiveDealWindows(List<Restaurant> restaurants) {
        return restaurants.stream()
                .flatMap(r -> r.deals().stream()
                        .map(d -> effectiveWindowResolver.calculateDealEffectiveTime(d, r.operatingHours())))
                .filter(Objects::nonNull)
                .toList();
    }
}
