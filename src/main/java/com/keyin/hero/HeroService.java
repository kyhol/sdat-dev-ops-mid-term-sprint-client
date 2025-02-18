package com.keyin.hero;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@Service
public class HeroService {
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private HeroDTO currentHero;

    public HeroService(@Value("${api.base-url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl + "/api/heroes";
    }

    public HeroDTO createHero(String name) {
        Map<String, String> request = Map.of("name", name);
        currentHero = restTemplate.postForObject(baseUrl, request, HeroDTO.class);
        return currentHero;
    }

    public HeroDTO getCurrentHero() {
        if (currentHero == null || currentHero.getId() == null) {
            return null;
        }
        String url = baseUrl + "/" + currentHero.getId();
        currentHero = restTemplate.getForObject(url, HeroDTO.class);
        return currentHero;
    }
}