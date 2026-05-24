package com.beesmart.model.events;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("6h")
public class HumidityEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long hiveId;
    private double humidity;
    private Date timestamp;

    public HumidityEvent() {
        this.timestamp = new Date();
    }

    public HumidityEvent(Long hiveId, double humidity) {
        this.hiveId = hiveId;
        this.humidity = humidity;
        this.timestamp = new Date();
    }

    public Long getHiveId() { return hiveId; }
    public void setHiveId(Long hiveId) { this.hiveId = hiveId; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
