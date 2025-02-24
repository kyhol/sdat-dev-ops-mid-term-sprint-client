package com.keyin.plushie;

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
public class PlushieServiceTest {

    private PlushieService plushieService;
    private final String baseUrl = "http://localhost:8080";

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() throws Exception {
        plushieService = new PlushieService(baseUrl);
        setPrivateField(plushieService, "client", mockHttpClient);
        realObjectMapper = new ObjectMapper();
    }

    @Test
    void getAllPlushies_ShouldReturnListOfPlushies() throws Exception {
        List<PlushieDTO> expectedPlushies = new ArrayList<>();
        expectedPlushies.add(new PlushieDTO(1L, "Bear", "Brown", false, "Soft"));
        expectedPlushies.add(new PlushieDTO(2L, "Rabbit", "White", false, "Fluffy"));

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(realObjectMapper.writeValueAsString(expectedPlushies));
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        List<PlushieDTO> result = plushieService.getAllPlushies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Bear", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Rabbit", result.get(1).getName());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/plushie", uri.toString());
            assertEquals("GET", request.method());
            return true;
        }), any());
    }

    @Test
    void getAllPlushies_WhenServerReturnsError_ShouldThrowException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            plushieService.getAllPlushies();
        });

        assertTrue(exception.getMessage().contains("Failed to get plushies"));
    }

    @Test
    void collectPlushie_ShouldSendPutRequest() throws Exception {
        Long plushieId = 1L;
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertDoesNotThrow(() -> plushieService.collectPlushie(plushieId));

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/plushie/" + plushieId + "/collect", uri.toString());
            assertEquals("PUT", request.method());
            return true;
        }), any());
    }

    @Test
    void collectPlushie_WhenServerReturnsError_ShouldThrowException() throws Exception {
        Long plushieId = 1L;
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            plushieService.collectPlushie(plushieId);
        });

        assertTrue(exception.getMessage().contains("Failed to collect plushie"));
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