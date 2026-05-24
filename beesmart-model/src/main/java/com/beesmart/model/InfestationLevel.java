package com.beesmart.model;

import java.io.Serializable;

public class InfestationLevel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String level;  // "high", "medium", "low"

    public InfestationLevel() {}
    public InfestationLevel(String level) { this.level = level; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}
