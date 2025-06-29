package com.kylemilner.eatclub.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.kylemilner.eatclub.exception.EatClubClientException;
import com.kylemilner.eatclub.mapper.RestaurantMapper;
import com.kylemilner.eatclub.model.Restaurant;
import com.kylemilner.eatclub.model.external.RestaurantDtoWrapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EatClubClient {

    private final RestClient restClient;
    private final RestaurantMapper restaurantMapper;

    private static final String RESTAURANTS_ENDPOINT = "/misc/challengedata.json";

    public List<Restaurant> getRestaurants() {
        RestaurantDtoWrapper response = fetchRestaurants();
        if (response == null || response.restaurants() == null) {
            return List.of();
        }
        return response.restaurants().stream()
                .map(restaurantMapper::toRestaurant)
                .toList();
    }

    RestaurantDtoWrapper fetchRestaurants() {
        return restClient.get().uri(RESTAURANTS_ENDPOINT)
                .retrieve()
                .onStatus(err -> err.is4xxClientError() || err.is5xxServerError(),
                        (req, res) -> {
                            throw new EatClubClientException(
                                    "Failed to fetch restaurants from Eatclub: " + res.getStatusCode());
                        })
                .body(RestaurantDtoWrapper.class);
    }

}
