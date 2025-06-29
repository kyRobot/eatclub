package com.kylemilner.eatclub.model;

import java.util.List;

public record Restaurant(
        String id,
        String name,
        String address1,
        String suburb,
        TimeRange openWindow,
        List<Deal> deals) {
}