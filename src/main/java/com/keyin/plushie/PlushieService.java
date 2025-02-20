package com.keyin.plushie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PlushieService {
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public PlushieService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
    }

    public List<PlushieDTO> getAllPlushies() throws Exception {
        URL url = new URL(baseUrl + "/plushie");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return objectMapper.readValue(
                    response.toString(),
                    new TypeReference<List<PlushieDTO>>(){}
            );
        } else {
            throw new Exception("Failed to fetch plushies: " + responseCode);
        }
    }
}