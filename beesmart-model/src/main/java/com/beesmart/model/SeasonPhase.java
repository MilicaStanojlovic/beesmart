package com.beesmart.model;

import java.io.Serializable;

public class SeasonPhase implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name; // "EARLY_SPRING", "SPRING_DEVELOPMENT", "MAIN_PASTURE", etc.

    public SeasonPhase() {}

    public SeasonPhase(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}