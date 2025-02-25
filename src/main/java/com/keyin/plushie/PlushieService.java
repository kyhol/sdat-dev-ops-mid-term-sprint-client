package com.keyin.plushie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PlushieService {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public PlushieService(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl + "/plushie";
    }

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

    public void resetAllPlushies() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reset"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to reset plushies: HTTP " + response.statusCode());
        }
    }
}