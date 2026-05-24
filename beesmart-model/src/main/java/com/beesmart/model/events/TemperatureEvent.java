package com.beesmart.model.events;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("2h")
public class TemperatureEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private double temperature;
    private String zone;          // "brood", "super", "external"
    private Date timestamp;

    public TemperatureEvent() {
        this.timestamp = new Date();
    }

    public TemperatureEvent(Long hiveId, double temperature, String zone) {
        this.hiveId = hiveId;
        this.temperature = temperature;
        this.zone = zone;
        this.timestamp = new Date();
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
