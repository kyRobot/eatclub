package com.kylemilner.eatclub.configuration;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.kylemilner.eatclub.client.EatClubClient;

@TestConfiguration
public class TestRestConfig {

    @Bean
    @Primary
    public EatClubClient eatClubClient() {
        return mock(EatClubClient.class);
    }
}
