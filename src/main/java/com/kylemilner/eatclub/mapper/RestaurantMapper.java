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
        TimeRange dealTime = calculateDealOverrideTime(d, restaurantOpenClose);

        return new Deal(d.objectId(),
                Integer.parseInt(d.discount()),
                Boolean.parseBoolean(d.dineIn()),
                Boolean.parseBoolean(d.lightning()),
                Integer.parseInt(d.qtyLeft()),
                dealTime);
    }

    private TimeRange toTimeRange(String start, String end) {
        return new TimeRange(TimeUtil.parseAmPmTime(start), TimeUtil.parseAmPmTime(end));
    }

    private TimeRange calculateDealOverrideTime(DealDto d, TimeRange restaurantOpenClose) {
        // dealDto can have both start and end or just open close, so we handle both
        // cases here.
        // arbitrarily we prefer start/end over open/close
        String startRaw = Optional.ofNullable(d.start()).orElse(d.open());
        String endRaw = Optional.ofNullable(d.end()).orElse(d.close());

        TimeRange dealWindow = startRaw != null && endRaw != null
                ? toTimeRange(startRaw, endRaw)
                : null;

        return dealWindow;
    }
}
