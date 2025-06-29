package com.kylemilner.eatclub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

class PeakServiceTest {
    private EatClubClient eatClubClient;
    private EffectiveWindowResolver effectiveWindowResolver;
    private PeakService peakService;

    @BeforeEach
    void setUp() {
        eatClubClient = mock(EatClubClient.class);
        effectiveWindowResolver = mock(EffectiveWindowResolver.class);
        peakService = new PeakService(eatClubClient, effectiveWindowResolver, 60);
    }

    @Test
    void returnsNullWhenNoDeals() {
        when(eatClubClient.getRestaurants()).thenReturn(List.of());
        assertNull(peakService.findPeakWindow());
    }

    @Test
    void findsPeakWindowForSingleDeal() {
        TimeRange effectiveTime = new TimeRange(LocalTime.of(12, 0), LocalTime.of(15, 0));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", effectiveTime, List.of(
                new Deal("d1", 20, true, false, 10, null)));

        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(effectiveWindowResolver.calculateDealEffectiveTime(any(), any())).thenCallRealMethod();
        TimeRange peak = peakService.findPeakWindow();
        TimeRange expectedPeak = new TimeRange(LocalTime.of(12, 0), LocalTime.of(13, 0));
        assertEquals(expectedPeak, peak);
    }

    @Test
    void findsPeakWindowForMultipleOverlappingDeals() {
        // Two deals overlap from 13:00 to 14:00
        TimeRange hours = new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0));
        Deal d1 = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(12, 0), LocalTime.of(15, 0)));
        Deal d2 = new Deal("d2", 15, true, false, 5, new TimeRange(LocalTime.of(13, 0), LocalTime.of(16, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", hours, List.of(d1, d2));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(effectiveWindowResolver.calculateDealEffectiveTime(any(), any())).thenCallRealMethod();
        TimeRange peak = peakService.findPeakWindow();
        TimeRange expectedPeak = new TimeRange(LocalTime.of(13, 0), LocalTime.of(14, 0));
        assertEquals(expectedPeak, peak);
    }

    @Test
    void findsPeakWindowForNonOverlappingDeals() {
        // Two deals, no overlap, earliest window wins
        TimeRange hours = new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0));
        Deal d1 = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0)));
        Deal d2 = new Deal("d2", 15, true, false, 5, new TimeRange(LocalTime.of(15, 0), LocalTime.of(16, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", hours, List.of(d1, d2));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(effectiveWindowResolver.calculateDealEffectiveTime(any(), any())).thenCallRealMethod();
        TimeRange peak = peakService.findPeakWindow();
        TimeRange expectedPeak = new TimeRange(LocalTime.of(10, 0), LocalTime.of(11, 0));
        assertEquals(expectedPeak, peak);
    }

    @Test
    void findsPeakWindowOverMidnight() {
        // Deal active from 22:00 to 02:00 (overnight)
        TimeRange hours = new TimeRange(LocalTime.of(20, 0), LocalTime.of(4, 0));
        Deal d1 = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(22, 0), LocalTime.of(2, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", hours, List.of(d1));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(effectiveWindowResolver.calculateDealEffectiveTime(any(), any())).thenCallRealMethod();
        TimeRange peak = peakService.findPeakWindow();
        TimeRange expectedPeak = new TimeRange(LocalTime.of(22, 0), LocalTime.of(23, 0));
        assertEquals(expectedPeak, peak);
    }

    @Test
    void findsPeakWindowWrapsOverMidnight() {
        // Deal active from 23:30 to 01:30 (overnight, not on the hour)
        TimeRange hours = new TimeRange(LocalTime.of(20, 0), LocalTime.of(4, 0));
        Deal d1 = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(23, 30), LocalTime.of(1, 30)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", hours, List.of(d1));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(effectiveWindowResolver.calculateDealEffectiveTime(any(), any())).thenCallRealMethod();
        peakService = new PeakService(eatClubClient, effectiveWindowResolver, 60); // 1 hour window
        TimeRange peak = peakService.findPeakWindow();
        TimeRange expectedPeak = new TimeRange(LocalTime.of(23, 30), LocalTime.of(0, 30));
        assertEquals(expectedPeak, peak);
    }

    @Test
    void findsPeakWindowWithMultipleRestaurants() {
        // Two restaurants, overlapping deals
        TimeRange hours = new TimeRange(LocalTime.of(10, 0), LocalTime.of(18, 0));
        Deal d1 = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(12, 0), LocalTime.of(15, 0)));
        Restaurant r1 = new Restaurant("r1", "Test Restaurant 1", "123 Test St", "Testville", hours, List.of(d1));
        Deal d2 = new Deal("d2", 15, true, false, 5, new TimeRange(LocalTime.of(13, 0), LocalTime.of(16, 0)));
        Restaurant r2 = new Restaurant("r2", "Test Restaurant 2", "456 Test Ave", "Testville", hours, List.of(d2));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r1, r2));
        when(effectiveWindowResolver.calculateDealEffectiveTime(any(), any())).thenCallRealMethod();
        TimeRange peak = peakService.findPeakWindow();
        TimeRange expectedPeak = new TimeRange(LocalTime.of(13, 0), LocalTime.of(14, 0));
        assertEquals(expectedPeak, peak);
    }

}
