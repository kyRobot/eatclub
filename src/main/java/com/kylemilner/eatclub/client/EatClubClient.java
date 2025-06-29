package com.kylemilner.eatclub.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.kylemilner.eatclub.model.Restaurant;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EatClubClient {

    public List<Restaurant> getRestaurants() {
        return List.of();
    }

}
