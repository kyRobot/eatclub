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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.exception.EatClubClientException;
import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;

@SpringBootTest
@AutoConfigureMockMvc
class PeakIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EatClubClient eatClubClient;

    @Test
    void getPeakEndpoint_works() throws Exception {
        when(eatClubClient.getRestaurants()).thenReturn(List.of(restaurant()));
        mockMvc.perform(get("/peak?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peakTimeStart").value("9:00am"))
                .andExpect(jsonPath("$.peakTimeEnd").value("5:00pm"));
    }

    @Test
    void getPeakEndpoint_rejectsNotFound() throws Exception {
        when(eatClubClient.getRestaurants()).thenReturn(List.of());
        mockMvc.perform(get("/peak"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPeakEndpoint_badGateway() throws Exception {
        when(eatClubClient.getRestaurants()).thenThrow(new EatClubClientException("nope"));
        mockMvc.perform(get("/peak"))
                .andExpect(status().isBadGateway());
    }

    private Restaurant restaurant() {
        TimeRange t = new TimeRange(LocalTime.of(9, 0), LocalTime.of(17, 0));
        Deal d = new Deal("d1", 20, true, false, 10, null);
        Restaurant r = new Restaurant("r1", "Test Restaurant", "123 Test St", "Testville", t, List.of(d));
        return r;
    }

}
