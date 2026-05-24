package com.beesmart.model;

import java.io.Serializable;

public class DiseaseSuspicion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String disease;
    private String level;  // "high", "medium", "low"

    public DiseaseSuspicion() {}

    public DiseaseSuspicion(String disease, String level) {
        this.disease = disease;
        this.level = level;
    }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}

