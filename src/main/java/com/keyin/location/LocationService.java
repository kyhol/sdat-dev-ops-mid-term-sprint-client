package com.keyin.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keyin.plushie.PlushieDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * This client-side LocationService is a REST wrapper that communicates with the server.
 * It provides methods to:
 * - Fetch all locations.
 * - Get the count of completed locations.
 * - Reset all locations.
 * - Mark a location as completed and award a plushie.
 *
 */
public class LocationService {
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    /**
     * Constructs a new LocationService.
     * @param baseUrl The base URL of your server (e.g., "http://localhost:8080")
     */
    public LocationService(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl + "/location";
    }

    /**
     * Fetches all locations from the server.
     * @return A list of LocationDTO objects.
     * @throws Exception if the HTTP call fails.
     */
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

    /**
     * Returns the count of locations that are completed.
     * @return The count of completed locations.
     */
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

    /**
     * Resets all locations on the server.
     * @throws Exception if the HTTP call fails.
     */
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

    /**
     * Marks a location as completed on the server.
     * Also adds a plushie (based on the location name) to the collectedPlushies list.
     * @param locationId The ID of the location to complete.
     * @param allLocations The list of all locations (to find the location name).
     * @return true if successful, false otherwise.
     */
    public boolean completeLocation(Long locationId, List<LocationDTO> allLocations) {
        try {
            // Create the JSON body for the request
            String jsonInputString = "{\"id\":" + locationId + ",\"completed\":true}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/" + locationId))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // Find the location name in the list and add the plushie
//                for (LocationDTO loc : allLocations) {
//                    if (loc.getId().equals(locationId)) {
//                        collectedPlushies.add();
//                        break;
//                    }
//                }
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