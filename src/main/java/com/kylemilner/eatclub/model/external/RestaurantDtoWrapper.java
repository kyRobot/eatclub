package com.kylemilner.eatclub.model.external;

import java.util.List;

public record RestaurantDtoWrapper(
        List<RestaurantDto> restaurants) {
}
