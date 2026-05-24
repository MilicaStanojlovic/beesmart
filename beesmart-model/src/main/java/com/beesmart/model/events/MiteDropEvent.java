package com.beesmart.model.events;
import org.kie.api.definition.type.Role;
import java.io.Serializable;

@Role(Role.Type.EVENT)
public class MiteDropEvent implements Serializable {
    private int count; // Broj grinja

    public MiteDropEvent() {}
    public MiteDropEvent(int count) { this.count = count; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}