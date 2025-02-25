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
import static org.mockito.ArgumentMatchers.argThat;
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
        assertEquals(1L, heroService.getCurrentHeroId());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/api/heroes/1", uri.toString());
            assertEquals("PUT", request.method());

            assertTrue(request.headers().map().containsKey("Content-Type"));
            assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(""));

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
        assertEquals(2L, heroService.getCurrentHeroId());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertEquals(baseUrl + "/api/heroes", uri.toString());
            assertEquals("POST", request.method());

            assertTrue(request.headers().map().containsKey("Content-Type"));
            assertEquals("application/json", request.headers().firstValue("Content-Type").orElse(""));

            return true;
        }), any());
    }

    @Test
    void getCurrentHero_WhenHeroExists_ShouldReturnHero() throws Exception {
        HeroDTO expectedHero = new HeroDTO();
        expectedHero.setId(1L);
        expectedHero.setName("Flash");

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(realObjectMapper.writeValueAsString(expectedHero));

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HeroDTO result = heroService.getCurrentHero();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Flash", result.getName());

        verify(mockHttpClient).send(argThat(request -> {
            URI uri = request.uri();
            assertTrue(uri.toString().startsWith(baseUrl + "/api/heroes/1?t="));
            assertEquals("GET", request.method());
            return true;
        }), any());
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

    @Test
    void createHero_WhenServerReturnsError_ShouldThrowException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            heroService.createHero("Batman");
        });

        assertTrue(exception.getMessage().contains("Failed to create hero"));
    }

    @Test
    void getCurrentHero_WhenServerReturnsError_ShouldThrowException() throws Exception {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            heroService.getCurrentHero();
        });

        assertTrue(exception.getMessage().contains("Failed to get hero"));
    }

    @Test
    void setCurrentHeroId_ShouldUpdateCurrentHeroId() {
        Long newHeroId = 5L;
        heroService.setCurrentHeroId(newHeroId);

        assertEquals(newHeroId, heroService.getCurrentHeroId());
    }

    @Test
    void setCurrentHeroId_WithNullValue_ShouldNotUpdateCurrentHeroId() {
        Long initialId = heroService.getCurrentHeroId();
        heroService.setCurrentHeroId(null);

        assertEquals(initialId, heroService.getCurrentHeroId());
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