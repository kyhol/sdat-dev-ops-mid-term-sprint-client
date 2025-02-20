package com.keyin.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    private LocationService locationService;
    private final String baseUrl = "http://localhost:8080";

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() throws Exception {
        locationService = new LocationService(baseUrl);

        setPrivateField(locationService, "client", mockHttpClient);

        realObjectMapper = new ObjectMapper();
    }

    @Test
    void moveToNextLocation_ShouldReturnLocationName() throws Exception {
        Long heroId = 1L;
        String expectedLocation = "Cave of Wonders";

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(expectedLocation);

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String result = locationService.moveToNextLocation(heroId);

        assertNotNull(result);
        assertEquals(expectedLocation, result);

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/location/next/1", uri.toString());

            assertEquals("POST", request.method());

            return true;
        }), any());
    }

    @Test
    void getAllLocations_ShouldReturnListOfLocations() throws Exception {
        List<LocationDTO> expectedLocations = new ArrayList<>();
        LocationDTO location1 = new LocationDTO();
        location1.setId(1L);
        location1.setName("Forest");
        LocationDTO location2 = new LocationDTO();
        location2.setId(2L);
        location2.setName("Mountain");
        expectedLocations.add(location1);
        expectedLocations.add(location2);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(realObjectMapper.writeValueAsString(expectedLocations));

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        List<LocationDTO> result = locationService.getAllLocations();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Forest", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Mountain", result.get(1).getName());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/location", uri.toString());

            assertEquals("GET", request.method());

            return true;
        }), any());
    }

    @Test
    void moveToNextLocation_WhenServerReturnsError_ShouldThrowException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            locationService.moveToNextLocation(1L);
        });

        assertTrue(exception.getMessage().contains("Failed to move to next location"));
    }

    @Test
    void getAllLocations_WhenServerReturnsError_ShouldThrowException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            locationService.getAllLocations();
        });

        assertTrue(exception.getMessage().contains("Failed to get locations"));
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Could not set field: " + fieldName, e);
        }
    }
}