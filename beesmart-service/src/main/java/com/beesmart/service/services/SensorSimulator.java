package com.beesmart.service.services;

import com.beesmart.model.Alarm;
import com.beesmart.model.Bloom;
import com.beesmart.model.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
@Service
public class SensorSimulator {

    private final CepService cepService;
    private final Random random = new Random();

    private String activeScenario = "NORMAL";
    private int tickCount = 0;
    private boolean running = false;

    private double baseTemp = 35.0;
    private double baseSound = 55.0;
    private double baseFreq = 280.0;
    private double baseWeight = 42.0;
    private double baseHumidity = 60.0;
    private Long hiveId = 14L;
    private double lastTemp = 35.0;
    private double lastSound = 55.0;
    private double lastWeight = 42.0;
    private double lastHumidity = 60.0;
    private String currentPhase = "";
    @Autowired
    public SensorSimulator(CepService cepService) {
        this.cepService = cepService;
    }

    public String startScenario(String scenario, Long hiveId) {
        this.activeScenario = scenario.toUpperCase();
        this.hiveId = hiveId;
        this.tickCount = 0;
        this.running = true;

        baseTemp = 35.0;
        baseSound = 55.0;
        baseFreq = 280.0;
        baseWeight = 42.0;
        baseHumidity = 60.0;

        if ("ROBBING".equals(activeScenario)) {
            cepService.setPastureStatus("acacia", false);
            cepService.setPastureStatus("linden", false);
        }

        return "Simulator started: " + activeScenario + " on hive #" + hiveId;
    }

    public String stopSimulation() {
        this.running = false;
        this.activeScenario = "NORMAL";
        this.tickCount = 0;
        return "Simulator stopped";
    }

    public SimulatorStatus getStatus() {
        return new SimulatorStatus(running, activeScenario, hiveId, tickCount,
                cepService.getActiveAlarms(),
                lastTemp, lastSound, lastWeight, lastHumidity, currentPhase);
    }

    @Scheduled(fixedRate = 5000)
    public void tick() {
        if (!running) return;

        tickCount++;
        double noise = (random.nextDouble() - 0.5) * 0.4;

        switch (activeScenario) {
            case "NORMAL":
                sendNormalReadings(noise);
                break;
            case "SWARMING":
                sendSwarmingReadings(noise);
                break;
            case "ROBBING":
                sendRobbingReadings(noise);
                break;
            case "QUEEN_LOSS":
                sendQueenLossReadings(noise);
                break;
            case "WEIGHT_DROP":
                sendWeightDropReadings(noise);
                break;
            default:
                sendNormalReadings(noise);
        }

        System.out.println("[SIMULATOR] Tick " + tickCount +
                " | Scenario: " + activeScenario +
                " | Alarms: " + cepService.getActiveAlarms().size());
    }
    private void sendNormalReadings(double noise) {
        cepService.insertTemperature(hiveId, baseTemp + noise, "brood");
        cepService.insertTemperature(hiveId, 18.0 + noise, "external");
        cepService.insertSound(hiveId, baseSound + noise, baseFreq + noise * 10);
        cepService.insertWeight(hiveId, baseWeight + noise * 0.1);
        cepService.insertHumidity(hiveId, baseHumidity + noise * 2);

        // ─── čuvanje za frontend ───
        lastTemp = baseTemp + noise;
        lastSound = baseSound + noise;
        lastWeight = baseWeight + noise * 0.1;
        lastHumidity = baseHumidity + noise * 2;
        currentPhase = "Normal - stable readings";
    }
private void sendSwarmingReadings(double noise) {
    if (tickCount <= 3) {
        double temp = baseTemp + (tickCount * 1.0) + noise;
        cepService.insertTemperature(hiveId, temp, "brood");
        cepService.insertSound(hiveId, baseSound + (tickCount * 5), baseFreq + noise * 10);
        cepService.insertWeight(hiveId, baseWeight + noise * 0.1);
        lastTemp = temp;
        lastSound = baseSound + (tickCount * 5);
        lastWeight = baseWeight + noise * 0.1;
        currentPhase = "Phase 1 - Temperature rising";
    } else if (tickCount <= 6) {
        double sound = baseSound + (tickCount * 10);
        cepService.insertTemperature(hiveId, baseTemp + 3.0 + noise, "brood");
        cepService.insertSound(hiveId, sound, baseFreq + 50);
        cepService.insertWeight(hiveId, baseWeight + noise * 0.1);
        lastTemp = baseTemp + 3.0 + noise;
        lastSound = sound;
        lastWeight = baseWeight + noise * 0.1;
        currentPhase = "Phase 2 - Sound rising";
    } else {
        double weight = baseWeight - ((tickCount - 6) * 0.8);
        cepService.insertTemperature(hiveId, baseTemp + 3.0 + noise, "brood");
        cepService.insertSound(hiveId, baseSound + 60 + noise, baseFreq + 50);
        cepService.insertWeight(hiveId, weight);
        lastTemp = baseTemp + 3.0 + noise;
        lastSound = baseSound + 60 + noise;
        lastWeight = weight;
        currentPhase = "Phase 3 - Weight dropping";
        if (tickCount > 10) {
            running = false;
            currentPhase = "Scenario complete";
        }
    }
}


private void sendRobbingReadings(double noise) {
    double sound = baseSound + (tickCount * 3) + noise;
    double weight = baseWeight - (tickCount * 0.15);

    cepService.insertTemperature(hiveId, baseTemp + noise, "brood");
    cepService.insertSound(hiveId, sound, baseFreq + tickCount * 5);
    cepService.insertWeight(hiveId, weight);
    cepService.insertHumidity(hiveId, baseHumidity + noise * 2);

    // ─── čuvanje za frontend ───
    lastTemp = baseTemp + noise;
    lastSound = sound;
    lastWeight = weight;
    lastHumidity = baseHumidity + noise * 2;
    currentPhase = "Robbing - sound rising, weight dropping";

    System.out.println("  [ROBBING] Sound: " + String.format("%.1f", sound) +
            "dB, Weight: " + String.format("%.1f", weight) + "kg");

    if (tickCount > 12) {
        running = false;
        currentPhase = "Scenario complete";
        System.out.println("  [ROBBING] Scenario complete");
    }
}

private void sendQueenLossReadings(double noise) {
    double freq = baseFreq + (tickCount * 30) + noise * 10;
    if (freq > 600) freq = 600 + noise * 10;

    cepService.insertTemperature(hiveId, baseTemp + noise, "brood");
    cepService.insertSound(hiveId, baseSound + tickCount + noise, freq);
    cepService.insertWeight(hiveId, baseWeight + noise * 0.1);

    // ─── čuvanje za frontend ───
    lastTemp = baseTemp + noise;
    lastSound = baseSound + tickCount + noise;
    lastWeight = baseWeight + noise * 0.1;
    currentPhase = "Queen loss - frequency: " + String.format("%.0f", freq) + "Hz";

    System.out.println("  [QUEEN_LOSS] Frequency: " +
            String.format("%.0f", freq) + "Hz");

    if (tickCount > 15) {
        running = false;
        currentPhase = "Scenario complete";
        System.out.println("  [QUEEN_LOSS] Scenario complete");
    }
}

private void sendWeightDropReadings(double noise) {
    if (tickCount <= 2) {
        cepService.insertWeight(hiveId, baseWeight + noise * 0.1);
        lastWeight = baseWeight + noise * 0.1;
        currentPhase = "Weight drop - normal readings";
        System.out.println("  [WEIGHT_DROP] Normal: " +
                String.format("%.1f", baseWeight) + "kg");
    } else if (tickCount == 3) {
        double dropped = baseWeight - 7.0;
        cepService.insertWeight(hiveId, dropped);
        lastWeight = dropped;
        currentPhase = "Weight drop - DROPPED -7kg!";
        System.out.println("  [WEIGHT_DROP] DROPPED to " +
                String.format("%.1f", dropped) + "kg (-7kg!)");
    } else {
        cepService.insertWeight(hiveId, baseWeight - 7.0 + noise * 0.1);
        lastWeight = baseWeight - 7.0 + noise * 0.1;
        currentPhase = "Weight drop - staying low";
        if (tickCount > 5) {
            running = false;
            currentPhase = "Scenario complete";
            System.out.println("  [WEIGHT_DROP] Scenario complete");
        }
    }

    cepService.insertTemperature(hiveId, baseTemp + noise, "brood");
    cepService.insertSound(hiveId, baseSound + noise, baseFreq + noise * 10);

    // ─── čuvanje za frontend ───
    lastTemp = baseTemp + noise;
    lastSound = baseSound + noise;
}

    public static class SimulatorStatus {
        private boolean running;
        private String scenario;
        private Long hiveId;
        private int tickCount;
        private List<Alarm> activeAlarms;
        private double lastTemp;
        private double lastSound;
        private double lastWeight;
        private double lastHumidity;
        private String phase;

        public SimulatorStatus(boolean running, String scenario, Long hiveId,
                               int tickCount, List<Alarm> activeAlarms,
                               double lastTemp, double lastSound,
                               double lastWeight, double lastHumidity,
                               String phase) {
            this.running = running;
            this.scenario = scenario;
            this.hiveId = hiveId;
            this.tickCount = tickCount;
            this.activeAlarms = activeAlarms;
            this.lastTemp = lastTemp;
            this.lastSound = lastSound;
            this.lastWeight = lastWeight;
            this.lastHumidity = lastHumidity;
            this.phase = phase;
        }

        public boolean isRunning() { return running; }
        public String getScenario() { return scenario; }
        public Long getHiveId() { return hiveId; }
        public int getTickCount() { return tickCount; }
        public List<Alarm> getActiveAlarms() { return activeAlarms; }
        public double getLastTemp() { return lastTemp; }
        public double getLastSound() { return lastSound; }
        public double getLastWeight() { return lastWeight; }
        public double getLastHumidity() { return lastHumidity; }
        public String getPhase() { return phase; }
    }
}
