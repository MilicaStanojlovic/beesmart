package com.beesmart.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int rulesFired;
    private String seasonPhase;
    private List<SocietyStatus> conditions = new ArrayList<>();
    private List<Diagnosis> diagnoses = new ArrayList<>();
    private List<Recommendation> recommendations = new ArrayList<>();
    private List<Alarm> alarms = new ArrayList<>();

    public DiagnosisResult() {}

    public int getRulesFired() { return rulesFired; }
    public void setRulesFired(int rulesFired) { this.rulesFired = rulesFired; }

    public String getSeasonPhase() { return seasonPhase; }
    public void setSeasonPhase(String seasonPhase) { this.seasonPhase = seasonPhase; }

    public List<SocietyStatus> getConditions() { return conditions; }
    public void setConditions(List<SocietyStatus> conditions) { this.conditions = conditions; }

    public List<Diagnosis> getDiagnoses() { return diagnoses; }
    public void setDiagnoses(List<Diagnosis> diagnoses) { this.diagnoses = diagnoses; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }

    public List<Alarm> getAlarms() { return alarms; }
    public void setAlarms(List<Alarm> alarms) { this.alarms = alarms; }
}
