package com.kylemilner.eatclub.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kylemilner.eatclub.model.RestaurantDeal;
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

    private DealsResponse mapToApiResponse(List<RestaurantDeal> restaurantDeals) {
        List<DealsResponse.DealResponseItem> items = restaurantDeals.stream()
                .map(rd -> new DealsResponse.DealResponseItem(
                        rd.id(),
                        rd.name(),
                        rd.addressLine1(),
                        rd.suburb(),
                        TimeUtil.formatToAmPmTime(rd.operatingHours().start()),
                        TimeUtil.formatToAmPmTime(rd.operatingHours().end()),
                        rd.activeDeal().id(),
                        Integer.toString(rd.activeDeal().discount()),
                        Boolean.toString(rd.activeDeal().dineIn()),
                        Boolean.toString(rd.activeDeal().lightning()),
                        Integer.toString(rd.activeDeal().quantityLeft())))
                .toList();
        return new DealsResponse(items);
    }

}
