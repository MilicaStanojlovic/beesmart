package com.beesmart.model;

import java.io.Serializable;
import java.util.List;

public class DiagnosisRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Hive hive;
    private Weather weather;
    private List<Bloom> blooms;
    private List<Symptom> symptoms;
    private int miteDropCount;
    private List<TreatmentHistory> treatmentHistory;

    public DiagnosisRequest() {}

    public Hive getHive() { return hive; }
    public void setHive(Hive hive) { this.hive = hive; }

    public Weather getWeather() { return weather; }
    public void setWeather(Weather weather) { this.weather = weather; }

    public List<Bloom> getBlooms() { return blooms; }
    public void setBlooms(List<Bloom> blooms) { this.blooms = blooms; }

    public List<Symptom> getSymptoms() { return symptoms; }
    public void setSymptoms(List<Symptom> symptoms) { this.symptoms = symptoms; }

    public int getMiteDropCount() { return miteDropCount; }
    public void setMiteDropCount(int miteDropCount) { this.miteDropCount = miteDropCount; }

    public List<TreatmentHistory> getTreatmentHistory() { return treatmentHistory; }
    public void setTreatmentHistory(List<TreatmentHistory> treatmentHistory) { this.treatmentHistory = treatmentHistory; }
}