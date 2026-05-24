package com.beesmart.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Symptom implements Serializable {
    private String name; // npr. "deformed_wings", "smell_of_decay"
    private boolean specific; // true ako je specifičan za CCD/sindrom

//    public Symptom() {}
//    public Symptom(String name, boolean specific) {
//        this.name = name;
//        this.specific = specific;
//    }
    public Symptom(String name) {
        this.name = name;
    }
    // Getters and Setters
}