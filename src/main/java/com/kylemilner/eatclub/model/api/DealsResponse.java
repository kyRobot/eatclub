package com.kylemilner.eatclub.model.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DealsResponse {
    private List<DealResponseItem> deals = new ArrayList<>();

    @Data
    public static class DealResponseItem {
        private String restaurantObjectId;
        private String restaurantName;
        private String restaurantAddress1;
        @JsonProperty("restarantSuburb") // Note: keeping the typo from challenge spec
        private String restaurantSuburb;
        private String restaurantOpen;
        private String restaurantClose;
        private String dealObjectId;
        private String discount;
        private String dineIn;
        private String lightning;
        private String qtyLeft;
    }
}
