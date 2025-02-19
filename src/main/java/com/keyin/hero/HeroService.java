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

    public HeroService(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl + "/api/heroes";
    }

    public HeroDTO updateHero(String name) throws Exception {
        Long defaultHeroId = 1L;

        String jsonBody = objectMapper.writeValueAsString(Map.of("name", name));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + defaultHeroId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update hero: " + response.statusCode());
        }

        currentHero = objectMapper.readValue(response.body(), HeroDTO.class);
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
        return currentHero;
    }

    public HeroDTO getCurrentHero() throws Exception {
        if (currentHero == null || currentHero.getId() == null) {
            return null;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + currentHero.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get hero: " + response.statusCode());
        }

        currentHero = objectMapper.readValue(response.body(), HeroDTO.class);
        return currentHero;
    }
}