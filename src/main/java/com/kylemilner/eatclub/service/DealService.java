package com.kylemilner.eatclub.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kylemilner.eatclub.model.RestaurantDeals;

@Service
public class DealService {

    public List<RestaurantDeals> getActiveDealsAtTime(LocalTime timeOfDay) {
        List<RestaurantDeals> deals = List.of();
        return deals;
    }

}
