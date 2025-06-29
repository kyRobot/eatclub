package com.kylemilner.eatclub.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.RestaurantDeals;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {

    private final EatClubClient eatClubClient;

    public List<RestaurantDeals> getActiveDealsAtTime(LocalTime timeOfDay) {
        eatClubClient.getRestaurants(); // plactholder
        List<RestaurantDeals> deals = List.of();
        return deals;
    }

}
