package com.kylemilner.eatclub.client;

import com.kylemilner.eatclub.model.Restaurant;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EatClubClientTest {

    @Test
    void getRestaurants_returnsEmptyListByDefault() {
        EatClubClient client = new EatClubClient();
        List<Restaurant> restaurants = client.getRestaurants();
        assertNotNull(restaurants);
        assertTrue(restaurants.isEmpty());
    }
}
