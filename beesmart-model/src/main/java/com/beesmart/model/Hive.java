package com.beesmart.model;


import java.io.Serializable;

public class Hive implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String hiveType;          // "LR", "DB", "AZ"
    private String breed;             // "Carniolan", "Italian", "Buckfast"
    private int frameCount;           // total frames with bees
    private int broodFrameCount;      // frames with brood
    private double honeyStockKg;      // honey reserves in kg
    private boolean hasSuper;         // mediste on hive
    private boolean organicProduction;
    private int queenAgeMonths;
    private boolean weakEggLaying;
    private double weightKg;
    private double weightLastMonthKg;

    public Hive() {}

    public Hive(Long id, String hiveType, String breed) {
        this.id = id;
        this.hiveType = hiveType;
        this.breed = breed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHiveType() { return hiveType; }
    public void setHiveType(String hiveType) { this.hiveType = hiveType; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }

    public int getBroodFrameCount() { return broodFrameCount; }
    public void setBroodFrameCount(int broodFrameCount) { this.broodFrameCount = broodFrameCount; }

    public double getHoneyStockKg() { return honeyStockKg; }
    public void setHoneyStockKg(double honeyStockKg) { this.honeyStockKg = honeyStockKg; }

    public boolean isHasSuper() { return hasSuper; }
    public void setHasSuper(boolean hasSuper) { this.hasSuper = hasSuper; }

    public boolean isOrganicProduction() { return organicProduction; }
    public void setOrganicProduction(boolean organicProduction) { this.organicProduction = organicProduction; }

    public int getQueenAgeMonths() { return queenAgeMonths; }
    public void setQueenAgeMonths(int queenAgeMonths) { this.queenAgeMonths = queenAgeMonths; }

    public boolean isWeakEggLaying() { return weakEggLaying; }
    public void setWeakEggLaying(boolean weakEggLaying) { this.weakEggLaying = weakEggLaying; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getWeightLastMonthKg() { return weightLastMonthKg; }
    public void setWeightLastMonthKg(double weightLastMonthKg) { this.weightLastMonthKg = weightLastMonthKg; }

    public double getMonthlyWeightLoss() {
        return weightLastMonthKg - weightKg;
    }
}
