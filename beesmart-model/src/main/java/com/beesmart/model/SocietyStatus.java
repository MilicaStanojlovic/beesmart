package com.beesmart.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocietyStatus {
    private String status; // npr. "weak-spring", "swarming-mood"
    private boolean intervention; // true ako je potrebna akcija
    public SocietyStatus() {}
    public SocietyStatus(String status, boolean intervention) {
        this.status = status;
        this.intervention = intervention;
    }
    // Getters and Setters
}
