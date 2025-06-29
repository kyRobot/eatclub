package com.kylemilner.eatclub.model;

public record Deal(
        String id,
        int discount,
        boolean dineIn,
        boolean lightning,
        int quantityLeft,
        TimeRange availabilityRange) {
}
