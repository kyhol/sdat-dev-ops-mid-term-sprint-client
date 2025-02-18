package com.keyin.minigame;

public class MiniGameDTO {
    private Long id;
    private String name;
    private String description;
    private Long locationId;

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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLocationId() {
         return locationId;
    }
    public void setLocationId(Long locationId) {
         this.locationId = locationId;
     }
}
