package com.kylemilner.eatclub.service;

import com.kylemilner.eatclub.model.TimeRange;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class PeakServiceTest {
    @Test
    void findPeakWindow_returnsExpectedTimeRange() {
        PeakService service = new PeakService();
        TimeRange range = service.findPeakWindow();
        assertNotNull(range);
        assertEquals(LocalTime.of(12, 0), range.start());
        assertEquals(LocalTime.of(14, 0), range.end());
    }
}
