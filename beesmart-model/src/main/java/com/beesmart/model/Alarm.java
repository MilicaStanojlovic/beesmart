package com.beesmart.model;

import java.io.Serializable;
import java.util.Date;

public class Alarm implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private String type;      // "SWARMING", "ROBBING", "QUEEN_LOSS", "BROOD_TEMP", "WEIGHT_DROP"
    private String message;
    private String urgency;   // "URGENT", "HIGH", "MEDIUM"
    private Date timestamp;

    public Alarm() { this.timestamp = new Date(); }

    public Alarm(String message, String urgency) {
        this.message = message;
        this.urgency = urgency;
        this.timestamp = new Date();
    }

    public Alarm(Long hiveId, String message) {
        this.hiveId = hiveId;
        this.message = message;
        this.timestamp = new Date();
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "ALARM [" + urgency + "]: " + message;
    }
}
