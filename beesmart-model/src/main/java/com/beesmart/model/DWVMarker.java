package com.beesmart.model;


import java.io.Serializable;

public class DWVMarker implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean detected;

    public DWVMarker() {}
    public DWVMarker(boolean detected) { this.detected = detected; }

    public boolean isDetected() { return detected; }
    public void setDetected(boolean detected) { this.detected = detected; }
}
