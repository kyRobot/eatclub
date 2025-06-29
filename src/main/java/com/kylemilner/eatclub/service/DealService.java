package com.kylemilner.eatclub.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kylemilner.eatclub.client.EatClubClient;
import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.RestaurantDeal;
import com.kylemilner.eatclub.model.RestaurantDeal.DealSummary;
import com.kylemilner.eatclub.model.TimeRange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {

    private final EatClubClient eatClubClient;

    public List<RestaurantDeal> getActiveDealsAtTime(LocalTime timeOfDay) {
        return eatClubClient.getRestaurants().stream()
                .filter(r -> r.deals() != null)
                .flatMap(r -> r.deals()
                        .stream()
                        .filter(d -> isDealActive(d, r, timeOfDay))
                        .map(d -> toRestaurantDeal(r, d)))
                .toList();
    }

    private boolean isDealActive(Deal deal, Restaurant restaurant, LocalTime timeOfDay) {
        TimeRange effectiveTimeRange = calculateDealEffectiveTime(deal, restaurant.operatingHours());
        return effectiveTimeRange.contains(timeOfDay);
    }

    private TimeRange calculateDealEffectiveTime(Deal deal, TimeRange restaurantOpenClose) {
        // calculate effective deal time window. Use restaurant open/close hours if the
        // deal has no override

        TimeRange dealWindow = deal.availabilityRange() != null
                ? deal.availabilityRange()
                : restaurantOpenClose;

        // if the deal window is now outside the restaurant's open hours, we clamp it to
        // the restaurant's open hours
        TimeRange boundToRestaurantOpenClose = TimeRange.intersection(dealWindow, restaurantOpenClose);
        log.debug("Deal {} effective time window after bounding to restaurant hours: {}", deal.id(),
                boundToRestaurantOpenClose);

        if (boundToRestaurantOpenClose == null) {
            log.warn("Deal {} has no meaningful time window. Using restaurant open hours", deal.id());
            return restaurantOpenClose;
        }

        return boundToRestaurantOpenClose;
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
