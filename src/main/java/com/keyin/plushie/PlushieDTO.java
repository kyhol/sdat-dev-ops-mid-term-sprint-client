package com.keyin.plushie;

public class PlushieDTO {
    private Long id;
    private String name;
    private String description;
    private boolean collected;

    public PlushieDTO() {}

    public PlushieDTO(Long id, String name, String description, boolean collected) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.collected = collected;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCollected() { return collected; }
    public void setCollected(boolean collected) { this.collected = collected; }
}
