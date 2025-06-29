package com.kylemilner.eatclub.service;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.RestaurantDeal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DealServiceTest {
    EatClubClient eatClubClient = Mockito.mock(EatClubClient.class);
    DealService dealService = new DealService(eatClubClient);

    @Test
    void getActiveDealsAtTime_returnsEmptyListByDefault() {
        Mockito.when(eatClubClient.getRestaurants()).thenReturn(List.of());
        List<RestaurantDeal> deals = dealService.getActiveDealsAtTime(LocalTime.of(12, 0));
        assertNotNull(deals);
        assertTrue(deals.isEmpty());
    }
}
