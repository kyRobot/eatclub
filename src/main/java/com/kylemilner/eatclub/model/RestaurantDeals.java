package com.kylemilner.eatclub.model;

import java.util.List;

public record RestaurantDeals(
        String id,
        String name,
        String address1,
        String suburb,
        TimeRange operatingHours,
        List<DealSummary> activeDeals) {
    public record DealSummary(
            String id,
            int discount,
            boolean dineIn,
            boolean lightning,
            int quantityLeft) {
    }
}
