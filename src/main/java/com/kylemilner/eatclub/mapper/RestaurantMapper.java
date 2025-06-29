package com.kylemilner.eatclub.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.model.Deal;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.TimeRange;
import com.kylemilner.eatclub.model.external.DealDto;
import com.kylemilner.eatclub.model.external.RestaurantDto;
import com.kylemilner.eatclub.util.TimeUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestaurantMapper {

    public Restaurant toRestaurant(RestaurantDto dto) {
        TimeRange openCloseHours = toTimeRange(dto.open(), dto.close());

        List<Deal> deals = dto.deals().stream()
                .map(d -> toDeal(d, openCloseHours))
                .collect(Collectors.toList());

        return new Restaurant(dto.objectId(),
                dto.name(),
                dto.address1(),
                dto.suburb(),
                openCloseHours,
                deals);
    }

    private Deal toDeal(DealDto d, TimeRange restaurantOpenClose) {
        TimeRange effectiveDealTimeRange = calculateDealEffectiveTime(d, restaurantOpenClose);

        return new Deal(d.objectId(), d.discount(),
                Boolean.parseBoolean(d.dineIn()),
                Boolean.parseBoolean(d.lightning()),
                Integer.parseInt(d.qtyLeft()),
                effectiveDealTimeRange);
    }

    private TimeRange toTimeRange(String start, String end) {
        return new TimeRange(TimeUtil.parseAmPmTime(start), TimeUtil.parseAmPmTime(end));
    }

    private TimeRange calculateDealEffectiveTime(DealDto d, TimeRange restaurantOpenClose) {
        // calculate effective deal time window. Use restaurant open/close hours if the
        // deal has no
        // overrides
        // dealDto can have both start and end or just open close, so we handle both
        // cases here.
        // arbitrarily we prefer start/end over open/close
        String startRaw = Optional.ofNullable(d.start()).orElse(d.open());
        String endRaw = Optional.ofNullable(d.end()).orElse(d.close());

        TimeRange dealWindow = startRaw != null && endRaw != null
                ? toTimeRange(startRaw, endRaw)
                : restaurantOpenClose; // fallback to restaurant time
        log.debug("Deal {} has effective time window: {}", d.objectId(), dealWindow);

        // finally, if the deal is outside the restaurant's open hours, we clamp it to
        // the restaurant's open hours
        TimeRange boundToRestaurantOpenClose = TimeRange.intersection(dealWindow, restaurantOpenClose);
        log.debug("Deal {} effective time window after bounding to restaurant hours: {}", d.objectId(),
                boundToRestaurantOpenClose);

        if (boundToRestaurantOpenClose == null) {
            log.warn(
                    "Deal {} has no effective time window after bounding to restaurant hours, using restaurant open hours",
                    d.objectId());
            return restaurantOpenClose;
        }

        return boundToRestaurantOpenClose;
    }
}
