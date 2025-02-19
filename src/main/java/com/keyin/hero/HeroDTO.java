package com.keyin.hero;

public class HeroDTO {
    private Long id;
    private String name;
    private String createdAt;
    private Long currentLocationID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCurrentLocationID() {return currentLocationID; }

    public void setCurrentLocationID(Long currentLocationID) {this.currentLocationID = currentLocationID; }
}