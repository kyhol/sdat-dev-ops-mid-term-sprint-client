package com.keyin.plushie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * This client-side PlushieService is a REST wrapper that communicates with the server.
 * It provides methods to:
 * - Fetch all plushies
 * - Add new plushies
 * - Update existing plushies
 */
public class PlushieService {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Constructs a new PlushieService.
     *
     * @param baseUrl The base URL of your server (e.g., "http://localhost:8080")
     */
    public PlushieService(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl + "/plushie";
    }

    /**
     * Fetches all plushies from the server.
     *
     * @return A list of PlushieDTO objects.
     * @throws Exception if the HTTP call fails.
     */
    public List<PlushieDTO> getAllPlushies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get plushies: HTTP " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), new TypeReference<List<PlushieDTO>>() {
        });
    }

    /**
     * Marks a plushie as collected by updating its collected status to true.
     *
     * @param id The ID of the plushie to update.
     * @throws Exception if the HTTP request fails.
     */
    public void collectPlushie(Long id) throws Exception {
        String url = baseUrl + "/" + id + "/collect";

        String requestBody = objectMapper.writeValueAsString(new PlushieDTO(id, null, null, true, null));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to collect plushie: HTTP " + response.statusCode());
        }
    }
}