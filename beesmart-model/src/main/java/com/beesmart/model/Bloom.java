package com.beesmart.model;


import java.io.Serializable;

public class Bloom implements Serializable {
    private static final long serialVersionUID = 1L;

    private String plant;    // "willow", "orchard", "acacia", "linden"
    private boolean active;

    public Bloom() {}

    public Bloom(String plant, boolean active) {
        this.plant = plant;
        this.active = active;
    }

    public String getPlant() { return plant; }
    public void setPlant(String plant) { this.plant = plant; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
