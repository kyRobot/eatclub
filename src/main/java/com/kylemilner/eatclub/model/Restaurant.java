package com.kylemilner.eatclub.model;

import java.util.List;

public record Restaurant(
        String id,
        String name,
        String addressLine1,
        String suburb,
        TimeRange operatingHours,
        List<Deal> deals) {
}