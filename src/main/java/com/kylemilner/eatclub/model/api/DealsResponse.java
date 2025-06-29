package com.kylemilner.eatclub.model.api;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DealsResponse(List<DealResponseItem> deals) {

    public record DealResponseItem(
            String restaurantObjectId,
            String restaurantName,
            String restaurantAddress1,
            @JsonProperty("restarantSuburb") // Note: keeping the typo from challenge spec
            String restaurantSuburb,
            String restaurantOpen,
            String restaurantClose,
            String dealObjectId,
            String discount,
            String dineIn,
            String lightning,
            String qtyLeft) {
    }
}
