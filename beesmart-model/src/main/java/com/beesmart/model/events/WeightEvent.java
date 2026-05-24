package com.beesmart.model.events;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("24h")
public class WeightEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private double weightKg;
    private Date timestamp;

    public WeightEvent() {
        this.timestamp = new Date();
    }

    public WeightEvent(Long hiveId, double weightKg) {
        this.hiveId = hiveId;
        this.weightKg = weightKg;
        this.timestamp = new Date();
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}

