package com.beesmart.model;

import org.kie.api.definition.type.Position;
import java.io.Serializable;

public class Causes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Position(0)
    private String cause;

    @Position(1)
    private String effect;

    public Causes() {}

    public Causes(String cause, String effect) {
        this.cause = cause;
        this.effect = effect;
    }

    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }

    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Causes causes = (Causes) o;
        if (cause != null ? !cause.equals(causes.cause) : causes.cause != null) return false;
        return effect != null ? effect.equals(causes.effect) : causes.effect == null;
    }

    @Override
    public int hashCode() {
        int result = cause != null ? cause.hashCode() : 0;
        result = 31 * result + (effect != null ? effect.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Causes{" + cause + " -> " + effect + "}";
    }
}
