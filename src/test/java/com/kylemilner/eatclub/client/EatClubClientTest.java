package com.kylemilner.eatclub.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.kylemilner.eatclub.mapper.RestaurantMapper;
import com.kylemilner.eatclub.model.Restaurant;

class EatClubClientTest {

    @Test
    void getRestaurants_returnsEmptyListWhenResponseIsNull() {
        RestaurantMapper mapper = mock(RestaurantMapper.class);
        EatClubClient client = spy(new EatClubClient(null, mapper));
        doReturn(null).when(client).fetchRestaurants();

        List<Restaurant> restaurants = client.getRestaurants();
        assertNotNull(restaurants);
        assertTrue(restaurants.isEmpty());
    }
}
