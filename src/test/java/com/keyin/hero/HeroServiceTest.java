package com.keyin.hero;

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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HeroServiceTest {

    private HeroService heroService;
    private final String baseUrl = "http://localhost:8080";

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private ObjectMapper realObjectMapper;

    @BeforeEach
    void setUp() throws Exception {
        heroService = new HeroService(baseUrl);

        setPrivateField(heroService, "client", mockHttpClient);

        realObjectMapper = new ObjectMapper();
    }

    @Test
    void updateHero_ShouldReturnUpdatedHero() throws Exception {
        String heroName = "Jordan";
        HeroDTO expectedHero = new HeroDTO();
        expectedHero.setId(1L);
        expectedHero.setName(heroName);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(realObjectMapper.writeValueAsString(expectedHero));

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HeroDTO result = heroService.updateHero(heroName);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(heroName, result.getName());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/api/heroes/1", uri.toString());

            assertEquals("PUT", request.method());

            return true;
        }), any());
    }

    @Test
    void createHero_ShouldReturnCreatedHero() throws Exception {
        String heroName = "Wonder Boy";
        HeroDTO expectedHero = new HeroDTO();
        expectedHero.setId(2L);
        expectedHero.setName(heroName);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(realObjectMapper.writeValueAsString(expectedHero));

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HeroDTO result = heroService.createHero(heroName);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(heroName, result.getName());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/api/heroes", uri.toString());

            assertEquals("POST", request.method());

            return true;
        }), any());
    }

    @Test
    void getCurrentHero_WhenHeroExists_ShouldReturnHero() throws Exception {
        HeroDTO currentHero = new HeroDTO();
        currentHero.setId(3L);
        currentHero.setName("Flash");
        setPrivateField(heroService, "currentHero", currentHero);

        HeroDTO updatedHero = new HeroDTO();
        updatedHero.setId(3L);
        updatedHero.setName("Flash");

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(realObjectMapper.writeValueAsString(updatedHero));

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HeroDTO result = heroService.getCurrentHero();

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Flash", result.getName());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/api/heroes/3", uri.toString());

            assertEquals("GET", request.method());

            return true;
        }), any());
    }

    @Test
    void getCurrentHero_WhenNoCurrentHero_ShouldReturnNull() throws Exception {
        setPrivateField(heroService, "currentHero", null);

        HeroDTO result = heroService.getCurrentHero();

        assertNull(result);

        verifyNoInteractions(mockHttpClient);
    }

    @Test
    void updateHero_WhenServerReturnsError_ShouldThrowException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            heroService.updateHero("Superman");
        });

        assertTrue(exception.getMessage().contains("Failed to update hero"));
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