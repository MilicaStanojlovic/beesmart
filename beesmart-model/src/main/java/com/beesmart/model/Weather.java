package com.beesmart.model;

import java.io.Serializable;

public class Weather implements Serializable {
    private static final long serialVersionUID = 1L;
    private int month;                  // 1-12
    private double avgWeeklyTemp;
    private double forecast3DayTemp;
    private double currentTemp;

    public Weather() {}

    public Weather(int month, double avgWeeklyTemp, double currentTemp, double forecast3DayTemp) {
        this.month = month;
        this.avgWeeklyTemp = avgWeeklyTemp;
        this.currentTemp = currentTemp;
        this.forecast3DayTemp = forecast3DayTemp;
    }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public double getAvgWeeklyTemp() { return avgWeeklyTemp; }
    public void setAvgWeeklyTemp(double avgWeeklyTemp) { this.avgWeeklyTemp = avgWeeklyTemp; }

    public double getForecast3DayTemp() { return forecast3DayTemp; }
    public void setForecast3DayTemp(double forecast3DayTemp) { this.forecast3DayTemp = forecast3DayTemp; }

    public double getCurrentTemp() { return currentTemp; }
    public void setCurrentTemp(double currentTemp) { this.currentTemp = currentTemp; }
}
