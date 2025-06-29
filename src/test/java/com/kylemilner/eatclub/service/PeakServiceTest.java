package com.kylemilner.eatclub.service;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.util.EffectiveWindowResolver;

@ExtendWith(MockitoExtension.class)
class PeakServiceTest {

    @Mock
    EatClubClient eatClubClient;

    @Mock
    EffectiveWindowResolver effectiveWindowResolver;

    PeakService peakService;

    @BeforeEach
    void setUp() {
        peakService = new PeakService(eatClubClient, effectiveWindowResolver, 60);
        effectiveWindowResolver = new EffectiveWindowResolver();
    }

    @Test
    void findPeakWindow_returnsNullTimeRange() {
        TimeRange range = peakService.findPeakWindow();
        assertNull(range);
    }
}
