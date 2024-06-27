package com.betvictor.loremipsum.processing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Value("${app.config.lorem-ipsum-api-url}")
    private String loremIpsumApiUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(loremIpsumApiUrl)
                .build();
    }
}
