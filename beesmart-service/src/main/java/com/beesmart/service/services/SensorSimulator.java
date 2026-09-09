package com.beesmart.service.services;

import com.beesmart.model.Alarm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class SensorSimulator {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${beesmart.simulator.api-key}")
    private String apiKey;

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
            setPastureStatus("acacia", false);
            setPastureStatus("linden", false);
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
                getActiveAlarms(),
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
                " | Alarms: " + getActiveAlarms().size());
    }

    private void sendNormalReadings(double noise) {
        insertTemperature(hiveId, baseTemp + noise, "brood");
        insertTemperature(hiveId, 18.0 + noise, "external");
        insertSound(hiveId, baseSound + noise, baseFreq + noise * 10);
        insertWeight(hiveId, baseWeight + noise * 0.1);
        insertHumidity(hiveId, baseHumidity + noise * 2);

        lastTemp = baseTemp + noise;
        lastSound = baseSound + noise;
        lastWeight = baseWeight + noise * 0.1;
        lastHumidity = baseHumidity + noise * 2;
        currentPhase = "Normal - stable readings";
    }

    private void sendSwarmingReadings(double noise) {
        if (tickCount <= 3) {
            double temp = baseTemp + (tickCount * 1.0) + noise;
            insertTemperature(hiveId, temp, "brood");
            insertSound(hiveId, baseSound + (tickCount * 5), baseFreq + noise * 10);
            insertWeight(hiveId, baseWeight + noise * 0.1);
            lastTemp = temp;
            lastSound = baseSound + (tickCount * 5);
            lastWeight = baseWeight + noise * 0.1;
            currentPhase = "Phase 1 - Temperature rising";
        } else if (tickCount <= 6) {
            double sound = baseSound + (tickCount * 10);
            insertTemperature(hiveId, baseTemp + 3.0 + noise, "brood");
            insertSound(hiveId, sound, baseFreq + 50);
            insertWeight(hiveId, baseWeight + noise * 0.1);
            lastTemp = baseTemp + 3.0 + noise;
            lastSound = sound;
            lastWeight = baseWeight + noise * 0.1;
            currentPhase = "Phase 2 - Sound rising";
        } else {
            double weight = baseWeight - ((tickCount - 6) * 0.8);
            insertTemperature(hiveId, baseTemp + 3.0 + noise, "brood");
            insertSound(hiveId, baseSound + 60 + noise, baseFreq + 50);
            insertWeight(hiveId, weight);
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

        insertTemperature(hiveId, baseTemp + noise, "brood");
        insertSound(hiveId, sound, baseFreq + tickCount * 5);
        insertWeight(hiveId, weight);
        insertHumidity(hiveId, baseHumidity + noise * 2);

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

        insertTemperature(hiveId, baseTemp + noise, "brood");
        insertSound(hiveId, baseSound + tickCount + noise, freq);
        insertWeight(hiveId, baseWeight + noise * 0.1);

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
            insertWeight(hiveId, baseWeight + noise * 0.1);
            lastWeight = baseWeight + noise * 0.1;
            currentPhase = "Weight drop - normal readings";
            System.out.println("  [WEIGHT_DROP] Normal: " +
                    String.format("%.1f", baseWeight) + "kg");
        } else if (tickCount == 3) {
            double dropped = baseWeight - 7.0;
            insertWeight(hiveId, dropped);
            lastWeight = dropped;
            currentPhase = "Weight drop - DROPPED -7kg!";
            System.out.println("  [WEIGHT_DROP] DROPPED to " +
                    String.format("%.1f", dropped) + "kg (-7kg!)");
        } else {
            insertWeight(hiveId, baseWeight - 7.0 + noise * 0.1);
            lastWeight = baseWeight - 7.0 + noise * 0.1;
            currentPhase = "Weight drop - staying low";
            if (tickCount > 5) {
                running = false;
                currentPhase = "Scenario complete";
                System.out.println("  [WEIGHT_DROP] Scenario complete");
            }
        }

        insertTemperature(hiveId, baseTemp + noise, "brood");
        insertSound(hiveId, baseSound + noise, baseFreq + noise * 10);

        lastTemp = baseTemp + noise;
        lastSound = baseSound + noise;
    }

    // ─── REST pozivi ka /api/cep (umesto direktnog poziva CepService) ───

    private List<Alarm> insertTemperature(Long hiveId, double temperature, String zone) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("hiveId", String.valueOf(hiveId));
        p.add("temperature", String.valueOf(temperature));
        p.add("zone", zone);
        return postAlarms("/temperature", p);
    }

    private List<Alarm> insertSound(Long hiveId, double decibels, double frequency) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("hiveId", String.valueOf(hiveId));
        p.add("decibels", String.valueOf(decibels));
        p.add("frequency", String.valueOf(frequency));
        return postAlarms("/sound", p);
    }

    private List<Alarm> insertWeight(Long hiveId, double weightKg) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("hiveId", String.valueOf(hiveId));
        p.add("weightKg", String.valueOf(weightKg));
        return postAlarms("/weight", p);
    }

    private List<Alarm> insertHumidity(Long hiveId, double humidity) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("hiveId", String.valueOf(hiveId));
        p.add("humidity", String.valueOf(humidity));
        return postAlarms("/humidity", p);
    }

    private void setPastureStatus(String plant, boolean active) {
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("plant", plant);
        p.add("active", String.valueOf(active));
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl() + "/pasture").queryParams(p).toUriString();
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(authHeaders()), String.class);
    }

    private List<Alarm> getActiveAlarms() {
        ResponseEntity<Alarm[]> resp = restTemplate.exchange(
                baseUrl() + "/alarms", HttpMethod.GET, new HttpEntity<>(authHeaders()), Alarm[].class);
        return resp.getBody() != null ? Arrays.asList(resp.getBody()) : Collections.emptyList();
    }

    private List<Alarm> postAlarms(String path, MultiValueMap<String, String> params) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl() + path).queryParams(params).toUriString();
        ResponseEntity<Alarm[]> resp = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(authHeaders()), Alarm[].class);
        return resp.getBody() != null ? Arrays.asList(resp.getBody()) : Collections.emptyList();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        return headers;
    }

    private String baseUrl() {
        return "http://localhost:" + serverPort + "/api/cep";
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