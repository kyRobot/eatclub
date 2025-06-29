package com.kylemilner.eatclub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.model.api.PeakResponse;
import com.kylemilner.eatclub.service.PeakService;
import com.kylemilner.eatclub.util.TimeUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PeakController {

    private final PeakService peakService;

    /**
     * Task 2:
     * An API to return the single Peak time range with the most active deals
     */
    @GetMapping("/peak")
    public ResponseEntity<PeakResponse> getPeakWindow() {
        var peakRange = peakService.findPeakWindow();
        if (peakRange == null) {
            return ResponseEntity.notFound().build();
        }
        var response = mapToPeakResponse(peakRange);
        return ResponseEntity.ok(response);
    }

    private PeakResponse mapToPeakResponse(TimeRange peakRange) {
        return new PeakResponse(
                TimeUtil.formatToAmPmTime(peakRange.start()),
                TimeUtil.formatToAmPmTime(peakRange.end()));
    }
}
