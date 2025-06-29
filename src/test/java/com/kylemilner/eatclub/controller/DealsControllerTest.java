package com.kylemilner.eatclub.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kylemilner.eatclub.service.DealService;

@ExtendWith(MockitoExtension.class)
class DealsControllerTest {

    @Mock
    DealService dealService;
    @InjectMocks
    DealsController dealsController;

    @Test
    void getActiveDeals_returnsDealsResponse() {
        when(dealService.getActiveDealsAtTime(any())).thenReturn(List.of());
        // Simulate valid time string
        String validTime = "12:00";
        var response = dealsController.getActiveDeals(validTime);
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getDeals().isEmpty());
    }

    @Test
    void getActiveDeals_invalidTime_givesBadRequest() {
        String invalidTime = "25:00";
        var response = dealsController.getActiveDeals(invalidTime);
        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
