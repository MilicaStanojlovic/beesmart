package com.beesmart.model.events;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("48h")
public class SoundEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private double decibelLevel;
    private double frequencyHz;
    private Date timestamp;

    public SoundEvent() {
        this.timestamp = new Date();
    }

    public SoundEvent(Long hiveId, double decibelLevel, double frequencyHz) {
        this.hiveId = hiveId;
        this.decibelLevel = decibelLevel;
        this.frequencyHz = frequencyHz;
        this.timestamp = new Date();
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public double getDecibelLevel() { return decibelLevel; }
    public void setDecibelLevel(double decibelLevel) { this.decibelLevel = decibelLevel; }

    public double getFrequencyHz() { return frequencyHz; }
    public void setFrequencyHz(double frequencyHz) { this.frequencyHz = frequencyHz; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
