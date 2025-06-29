package com.kylemilner.eatclub;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.exception.EatClubClientException;
import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;

@SpringBootTest
@AutoConfigureMockMvc
class DealsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EatClubClient eatClubClient;

    @Test
    void getDealsEndpoint_works() throws Exception {
        when(eatClubClient.getRestaurants()).thenReturn(List.of());
        mockMvc.perform(get("/deals?timeOfDay=12:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals").exists());
    }

    @Test
    void getDealsEndpoint_rejectsBadTime() throws Exception {
        mockMvc.perform(get("/deals?timeOfDay=26:99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDealsEndpoint_badGateway() throws Exception {
        when(eatClubClient.getRestaurants()).thenThrow(new EatClubClientException("nope"));
        mockMvc.perform(get("/peak"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getDealsEndpoint_returnsActiveDeals() throws Exception {
        var deal = deal(times(12, 14));
        var restaurant = restaurant(List.of(deal), times(9, 17));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(restaurant));
        mockMvc.perform(get("/deals?timeOfDay=12:30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals[0].dealObjectId").value("d1"));
    }

    @Test
    void getDealsEndpoint_filtersInactiveDeals() throws Exception {
        var deal = deal(times(12, 14));
        var restaurant = restaurant(List.of(deal), times(10, 22));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(restaurant));
        mockMvc.perform(get("/deals?timeOfDay=15:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals").isEmpty());
    }

    @Test
    void getDealsEndpoint_multipleRestaurantsAndDeals() throws Exception {
        var deal1 = deal(times(12, 14));
        var deal2 = deal(times(13, 15));
        var r1 = restaurant(List.of(deal1), times(10, 22));
        var r2 = restaurant(List.of(deal2), times(10, 22));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r1, r2));
        mockMvc.perform(get("/deals?timeOfDay=13:30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals.length()").value(2));
    }

    @Test
    void getDealsEndpoint_multipleRestaurantsAndDealsWithOneInvalid() throws Exception {
        var deal1 = deal(times(12, 14));
        var deal2 = deal(times(13, 15));
        var deal3 = deal(times(10, 11));
        var r1 = restaurant(List.of(deal1), times(10, 22));
        var r2 = restaurant(List.of(deal2, deal3), times(10, 22));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(r1, r2));
        mockMvc.perform(get("/deals?timeOfDay=13:30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals.length()").value(2));
    }

    @Test
    void getDealsEndpoint_handlesOvernightDeals() throws Exception {
        var deal = deal(times(22, 2));
        var restaurant = restaurant(List.of(deal), times(20, 4));
        when(eatClubClient.getRestaurants()).thenReturn(List.of(restaurant));
        mockMvc.perform(get("/deals?timeOfDay=23:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals[0].dealObjectId").value("d1"));
        mockMvc.perform(get("/deals?timeOfDay=01:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals[0].dealObjectId").value("d1"));
        mockMvc.perform(get("/deals?timeOfDay=03:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deals").isEmpty());
    }

    private TimeRange times(int openHour, int closeHour) {
        return new TimeRange(LocalTime.of(openHour, 0), LocalTime.of(closeHour, 0));
    }

    private Deal deal(TimeRange withHours) {
        Deal d = new Deal("d1", 20, true, false, 10, withHours);
        return d;
    }

    private Restaurant restaurant(List<Deal> withDeals, TimeRange openAt) {
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", openAt, withDeals);
        return r;
    }

}
