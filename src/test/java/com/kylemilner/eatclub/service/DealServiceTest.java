package com.kylemilner.eatclub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.RestaurantDeal;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    EffectiveWindowResolver windowResolver;

    @Mock
    EatClubClient eatClubClient;

    @InjectMocks
    DealService dealService;

    @Test
    void getActiveDealsAtTime_returnsEmptyListByDefault() {
        Mockito.when(eatClubClient.getRestaurants()).thenReturn(List.of());
        List<RestaurantDeal> deals = dealService.getActiveDealsAtTime(LocalTime.of(12, 0));
        assertNotNull(deals);
        assertTrue(deals.isEmpty());
    }

    @Test
    void returnsNoDealsWhenNoneActive() {
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville",
                new TimeRange(LocalTime.of(10, 0), LocalTime.of(22, 0)), List.of());
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        List<RestaurantDeal> deals = dealService.getActiveDealsAtTime(LocalTime.of(12, 0));
        assertTrue(deals.isEmpty());
    }

    @Test
    void returnsActiveDealWithinTimeRange() {
        Deal d = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville",
                new TimeRange(LocalTime.of(10, 0), LocalTime.of(22, 0)), List.of(d));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(windowResolver.calculateDealEffectiveTime(eq(d), any())).thenReturn(d.availabilityRange());
        List<RestaurantDeal> deals = dealService.getActiveDealsAtTime(LocalTime.of(13, 0));
        assertEquals(1, deals.size());
        assertEquals("d1", deals.get(0).activeDeal().id());
    }

    @Test
    void doesNotReturnDealOutsideTimeRange() {
        Deal d = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville",
                new TimeRange(LocalTime.of(10, 0), LocalTime.of(22, 0)), List.of(d));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(windowResolver.calculateDealEffectiveTime(eq(d), any())).thenReturn(d.availabilityRange());
        List<RestaurantDeal> deals = dealService.getActiveDealsAtTime(LocalTime.of(15, 0));
        assertTrue(deals.isEmpty());
    }

    @Test
    void returnsMultipleActiveDeals() {
        Deal d1 = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0)));
        Deal d2 = new Deal("d2", 15, true, false, 5, new TimeRange(LocalTime.of(13, 0), LocalTime.of(15, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville",
                new TimeRange(LocalTime.of(10, 0), LocalTime.of(22, 0)), List.of(d1, d2));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(windowResolver.calculateDealEffectiveTime(eq(d1), any())).thenReturn(d1.availabilityRange());
        when(windowResolver.calculateDealEffectiveTime(eq(d2), any())).thenReturn(d2.availabilityRange());
        List<RestaurantDeal> deals = dealService.getActiveDealsAtTime(LocalTime.of(13, 30));
        assertEquals(2, deals.size());
    }

    @Test
    void handlesOvernightDeal() {
        Deal d = new Deal("d1", 20, true, false, 10, new TimeRange(LocalTime.of(22, 0), LocalTime.of(2, 0)));
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville",
                new TimeRange(LocalTime.of(20, 0), LocalTime.of(4, 0)), List.of(d));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r));
        when(windowResolver.calculateDealEffectiveTime(eq(d), any())).thenReturn(d.availabilityRange());
        // Should be active at 23:00 and 01:00, not at 3:00
        assertEquals(1, dealService.getActiveDealsAtTime(LocalTime.of(23, 0)).size());
        assertEquals(1, dealService.getActiveDealsAtTime(LocalTime.of(1, 0)).size());
        assertTrue(dealService.getActiveDealsAtTime(LocalTime.of(3, 0)).isEmpty());
    }
}
