package com.kylemilner.eatclub;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.kylemilner.eatclub.client.EatClubClient;

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
}
