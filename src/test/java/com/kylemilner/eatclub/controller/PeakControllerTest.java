package com.kylemilner.eatclub.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.model.api.PeakResponse;
import com.kylemilner.eatclub.service.PeakService;

@ExtendWith(MockitoExtension.class)
class PeakControllerTest {
    @Mock
    PeakService peakService;
    @InjectMocks
    PeakController peakController;

    @Test
    void getActiveDeals_returnsPeakResponse() {
        TimeRange range = new TimeRange(java.time.LocalTime.of(12, 0), java.time.LocalTime.of(14, 0));
        when(peakService.findPeakWindow()).thenReturn(range);
        ResponseEntity<PeakResponse> response = peakController.getPeakWindow();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("12:00pm", response.getBody().peakTimeStart());
        assertEquals("2:00pm", response.getBody().peakTimeEnd());
    }

    @Test
    void getActiveDeals_returnsNotFoundWhenNoPeak() {
        when(peakService.findPeakWindow()).thenReturn(null);
        ResponseEntity<PeakResponse> response = peakController.getPeakWindow();
        assertEquals(404, response.getStatusCode().value());
    }
}
