package com.beesmart.model;

import org.kie.api.definition.type.Position;
import java.io.Serializable;

public class BelongsToGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Position(0)
    private String item;

    @Position(1)
    private String group;

    public BelongsToGroup() {}

    public BelongsToGroup(String item, String group) {
        this.item = item;
        this.group = group;
    }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BelongsToGroup that = (BelongsToGroup) o;
        if (item != null ? !item.equals(that.item) : that.item != null) return false;
        return group != null ? group.equals(that.group) : that.group == null;
    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }
}
