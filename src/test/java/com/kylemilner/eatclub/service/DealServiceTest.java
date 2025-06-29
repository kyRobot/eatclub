package com.kylemilner.eatclub.service;

import com.kylemilner.eatclub.model.RestaurantDeals;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DealServiceTest {
    DealService dealService = new DealService();

    @Test
    void getActiveDealsAtTime_returnsEmptyListByDefault() {
        List<RestaurantDeals> deals = dealService.getActiveDealsAtTime(LocalTime.of(12, 0));
        assertNotNull(deals);
        assertTrue(deals.isEmpty());
    }
}
