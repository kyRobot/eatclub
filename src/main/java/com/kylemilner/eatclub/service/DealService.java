package com.kylemilner.eatclub.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.RestaurantDeal;
import com.kylemilner.eatclub.model.RestaurantDeal.DealSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {

    private final EatClubClient eatClubClient;

    public List<RestaurantDeal> getActiveDealsAtTime(LocalTime timeOfDay) {
        return eatClubClient.getRestaurants().stream()
                .filter(r -> r.deals() != null)
                .flatMap(r -> r.deals()
                        .stream()
                        .filter(d -> isDealActive(d, timeOfDay))
                        .map(d -> toRestaurantDeal(r, d)))
                .toList();
    }

    private boolean isDealActive(Deal deal, LocalTime timeOfDay) {
        return deal.activeWindow().contains(timeOfDay);
    }

    private RestaurantDeal toRestaurantDeal(Restaurant restaurant, Deal deal) {
        return new RestaurantDeal(
                restaurant.id(),
                restaurant.name(),
                restaurant.addressLine1(),
                restaurant.suburb(),
                restaurant.operatingHours(),
                new DealSummary(
                        deal.id(),
                        deal.discount(),
                        deal.dineIn(),
                        deal.lightning(),
                        deal.quantityLeft()));
    }

}
