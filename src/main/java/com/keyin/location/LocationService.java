package com.keyin.location;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;

@Service
public class LocationService {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public LocationService(@Value("${api.base-url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl + "/location";
    }

    public String moveToNextLocation(Long heroId) {
        String url = baseUrl + "/next/" + heroId;
        return restTemplate.postForObject(url, null, String.class);
    }

    public List<LocationDTO> getAllLocations() {
        ResponseEntity<LocationDTO[]> response = restTemplate.getForEntity(baseUrl, LocationDTO[].class);
        return Arrays.asList(response.getBody());
    }
}