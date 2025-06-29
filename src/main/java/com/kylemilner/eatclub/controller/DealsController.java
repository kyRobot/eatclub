package com.kylemilner.eatclub.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kylemilner.eatclub.model.RestaurantDeals;
import com.kylemilner.eatclub.model.api.DealsResponse;
import com.kylemilner.eatclub.service.DealService;
import com.kylemilner.eatclub.util.TimeUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DealsController {

    private final DealService dealService;

    /**
     * Task 1:
     * An API to return all active deals at a specified timeOfDay
     */
    @GetMapping("/deals")
    public ResponseEntity<DealsResponse> getActiveDeals(@RequestParam("timeOfDay") String wantedTime) {
        LocalTime timeOfDay = TimeUtil.parseQueryParam(wantedTime);
        if (timeOfDay == null) {
            return ResponseEntity.badRequest().build();
        }
        var activeRestaurantDeals = dealService.getActiveDealsAtTime(timeOfDay);
        var dealsResponse = mapToApiResponse(activeRestaurantDeals);
        return ResponseEntity.ok(dealsResponse);
    }

    private DealsResponse mapToApiResponse(List<RestaurantDeals> restaurantDeals) {
        DealsResponse dealsResponse = new DealsResponse();
        return dealsResponse;
    }

}
