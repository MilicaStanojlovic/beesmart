package com.beesmart.service.services;


import com.beesmart.model.Alarm;
import com.beesmart.model.Bloom;
import com.beesmart.model.events.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Service
public class CepService {

    private final KieContainer kieContainer;
    private KieSession cepSession;

    @Autowired
    public CepService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @PostConstruct
    public void init() {
        // CEP session is long-lived (STREAM mode)
        cepSession = kieContainer.newKieSession("cepSession");
        System.out.println("[CEP] Session initialized in STREAM mode");
    }

    @PreDestroy
    public void cleanup() {
        if (cepSession != null) {
            cepSession.dispose();
        }
    }

    /**
     * Insert a temperature reading from a sensor.
     */
    public List<Alarm> insertTemperature(Long hiveId, double temperature, String zone) {
        cepSession.insert(new TemperatureEvent(hiveId, temperature, zone));
        cepSession.fireAllRules();
        return getActiveAlarms();
    }

    /**
     * Insert a sound reading from a sensor.
     */
    public List<Alarm> insertSound(Long hiveId, double decibelLevel, double frequencyHz) {
        cepSession.insert(new SoundEvent(hiveId, decibelLevel, frequencyHz));
        cepSession.fireAllRules();
        return getActiveAlarms();
    }

    /**
     * Insert a weight reading from a sensor.
     */
    public List<Alarm> insertWeight(Long hiveId, double weightKg) {
        cepSession.insert(new WeightEvent(hiveId, weightKg));
        cepSession.fireAllRules();
        return getActiveAlarms();
    }

    /**
     * Insert a humidity reading from a sensor.
     */
    public List<Alarm> insertHumidity(Long hiveId, double humidity) {
        cepSession.insert(new HumidityEvent(hiveId, humidity));
        cepSession.fireAllRules();
        return getActiveAlarms();
    }

    /**
     * Set pasture status (needed for robbing detection).
     */
    public void setPastureStatus(String plant, boolean active) {
        cepSession.insert(new Bloom(plant, active));
        cepSession.fireAllRules();
    }

    /**
     * Get all currently active alarms.
     */
    public List<Alarm> getActiveAlarms() {
        List<Alarm> alarms = new ArrayList<>();
        for (Object obj : cepSession.getObjects()) {
            if (obj instanceof Alarm) {
                alarms.add((Alarm) obj);
            }
        }
        return alarms;
    }
}