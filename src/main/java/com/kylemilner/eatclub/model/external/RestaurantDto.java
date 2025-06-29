package com.kylemilner.eatclub.model.external;

import java.util.List;

public record RestaurantDto(
        String objectId,
        String name,
        String address1,
        String suburb,
        List<String> cuisines,
        String imageLink,
        String open,
        String close,
        List<DealDto> deals) {
}
