package com.keyin.hero;

import java.time.LocalDateTime;

public class HeroDTO {
    private Long id;
    private String name;
    private int lives = 3;
    private int collectedPlushies = 0;
    private String currentLocation;
    private LocalDateTime createdAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCollectedPlushies() { return collectedPlushies; }
    public void setCollectedPlushies(int collectedPlushies) { this.collectedPlushies = collectedPlushies; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}