package com.kylemilner.eatclub.service;

import java.time.LocalTime;

import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.model.TimeRange;

@Component
public class PeakService {

    public TimeRange findPeakWindow() {
        LocalTime peakStart = LocalTime.of(12, 0); // 12:00 PM
        LocalTime peakEnd = LocalTime.of(14, 0); // 2:00 PM
        return new TimeRange(peakStart, peakEnd);
    }

}
