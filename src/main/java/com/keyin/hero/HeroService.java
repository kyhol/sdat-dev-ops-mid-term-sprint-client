package com.keyin.hero;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HeroService {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private HeroDTO currentHero;
    private Long currentHeroId = 1L;

    public HeroService(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl + "/api/heroes";
    }

    public HeroDTO updateHero(String name) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(Map.of("name", name));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + currentHeroId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update hero: " + response.statusCode());
        }

        currentHero = objectMapper.readValue(response.body(), HeroDTO.class);
        // Update the ID in case it changed (though it shouldn't for updates)
        if (currentHero != null && currentHero.getId() != null) {
            currentHeroId = currentHero.getId();
            System.out.println("Updated hero ID: " + currentHeroId);
        }
        return currentHero;
    }

    public HeroDTO createHero(String name) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(Map.of("name", name));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to create hero: " + response.statusCode());
        }

        currentHero = objectMapper.readValue(response.body(), HeroDTO.class);
        // Save the ID for future use
        if (currentHero != null && currentHero.getId() != null) {
            currentHeroId = currentHero.getId();
            System.out.println("Set current hero ID to: " + currentHeroId);
        }
        return currentHero;
    }

    public HeroDTO getCurrentHero() throws Exception {
        String cacheParam = "?t=" + System.currentTimeMillis();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + currentHeroId + cacheParam))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Raw hero response: " + response.body());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get hero: " + response.statusCode());
        }

        currentHero = objectMapper.readValue(response.body(), HeroDTO.class);
        return currentHero;
    }

    public Long getCurrentHeroId() {
        return currentHeroId;
    }

    public void setCurrentHeroId(Long heroId) {
        if (heroId != null) {
            this.currentHeroId = heroId;
            System.out.println("Manually set hero ID to: " + heroId);
        }
    }
}