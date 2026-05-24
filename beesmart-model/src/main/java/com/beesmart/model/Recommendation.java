package com.beesmart.model;


import java.io.Serializable;

public class Recommendation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private String type;        // "FEEDING", "TREATMENT", "INSPECTION", "ANTI_SWARM", "QUEEN_REPLACE"
    private String text;
    private String urgency;     // "URGENT", "HIGH", "MEDIUM", "LOW"
    private String warning;
    private int checkInDays;

    public Recommendation() {}

    public Recommendation(String text, String urgency) {
        this.text = text;
        this.urgency = urgency;
    }

    public Recommendation(Long hiveId, String text) {
        this.hiveId = hiveId;
        this.text = text;
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getWarning() { return warning; }
    public void setWarning(String warning) { this.warning = warning; }

    public int getCheckInDays() { return checkInDays; }
    public void setCheckInDays(int checkInDays) { this.checkInDays = checkInDays; }

    @Override
    public String toString() {
        return "Recommendation{" + text + ", urgency=" + urgency +
                (warning != null ? ", WARNING: " + warning : "") + "}";
    }
}
