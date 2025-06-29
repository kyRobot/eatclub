package com.kylemilner.eatclub.model;

public record RestaurantDeal(
                String id,
                String name,
                String addressLine1,
                String suburb,
                TimeRange operatingHours,
                DealSummary activeDeal) {

        public record DealSummary(
                        String id,
                        int discount,
                        boolean dineIn,
                        boolean lightning,
                        int quantityLeft) {
        }
}
