package com.kylemilner.eatclub.model.external;

public record DealDto(
        String objectId,
        String discount,
        String dineIn,
        String lightning,
        String open,
        String close,
        String start,
        String end,
        String qtyLeft) {
}
