package com.beesmart.model;

import java.io.Serializable;

public class TreatmentHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private String treatmentName;
    private int daysAgo;
    private boolean isAntibiotic;

    public TreatmentHistory() {}

    public TreatmentHistory(Long hiveId, String treatmentName, int daysAgo, boolean isAntibiotic) {
        this.hiveId = hiveId;
        this.treatmentName = treatmentName;
        this.daysAgo = daysAgo;
        this.isAntibiotic = isAntibiotic;
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public int getDaysAgo() { return daysAgo; }
    public void setDaysAgo(int daysAgo) { this.daysAgo = daysAgo; }

    public boolean isAntibiotic() { return isAntibiotic; }
    public void setAntibiotic(boolean antibiotic) { isAntibiotic = antibiotic; }
}
