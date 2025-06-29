package com.kylemilner.eatclub.model;

public record Deal(
        String id,
        String discount,
        boolean dineIn,
        boolean lightning,
        int quantityLeft,
        TimeRange activeWindow) {
}
