package com.keyin.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keyin.plushie.PlushieDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class LocationService {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public LocationService(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl + "/location";
    }

    public List<LocationDTO> getAllLocations() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get locations: HTTP " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), new TypeReference<List<LocationDTO>>() {});
    }

    public int getCompletedLocationsCount() {
        try {
            List<LocationDTO> locations = getAllLocations();
            int count = 0;
            for (LocationDTO loc : locations) {
                if (loc.isCompleted()) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void resetAllLocations() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reset"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to reset locations: HTTP " + response.statusCode());
        }
    }

    public boolean completeLocation(Long locationId, List<LocationDTO> allLocations) {
        try {
            String jsonInputString = "{\"id\":" + locationId + ",\"completed\":true}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + locationId))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return true;
            } else {
                System.err.println("Complete location failed: HTTP " + response.statusCode());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}