package com.beesmart.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Diagnosis implements Serializable {
    private String disease; // npr. "Varroosis", "AmericanFoulbrood"
    private String urgency; // "URGENT", "HIGH", "MEDIUM"
    private boolean allSymptomsMet;

    public Diagnosis() {}
    public Diagnosis(String disease, String urgency, boolean allSymptomsMet) {
        this.disease = disease;
        this.urgency = urgency;
        this.allSymptomsMet = allSymptomsMet;
    }
    public Diagnosis(String disease) {
        this.disease = disease;
    }
    // Getters and Setters
}