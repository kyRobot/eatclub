package com.kylemilner.eatclub.util;

import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.TimeRange;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EffectiveWindowResolver {

    public TimeRange calculateDealEffectiveTime(Deal deal, TimeRange restaurantOpenClose) {
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
}
