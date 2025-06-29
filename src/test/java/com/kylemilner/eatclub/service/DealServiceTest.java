package com.kylemilner.eatclub.service;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.RestaurantDeal;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    EffectiveWindowResolver effectiveWindowResolver;

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
}
