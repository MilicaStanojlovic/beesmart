package com.beesmart.service;


import com.beesmart.model.Alarm;
import com.beesmart.model.events.TemperatureEvent;
import com.beesmart.model.events.SoundEvent;
import com.beesmart.model.events.WeightEvent;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * To use pseudo clock for testing, change kmodule.xml cepSession:
 *   clockType="pseudo" (instead of "realtime")
 *
 * Or create a test kmodule.xml in src/test/resources/META-INF/
 */
@SpringBootTest(classes = BeeSmartApplication.class)
public class CepRulesTest {

    @Autowired
    private KieContainer kieContainer;

    @Test
    public void testSwarmingDetection() {
        // Use cepSession (STREAM mode)
        KieSession ks = kieContainer.newKieSession("cepSession");

        // Note: For real temporal testing, use pseudo clock.
        // This test demonstrates the concept - in production,
        // events arrive via REST endpoints with real timestamps.

        Long hiveId = 14L;

        // Insert temperature events (rise >2°C)
        ks.insert(new TemperatureEvent(hiveId, 35.0, "brood"));
        ks.insert(new TemperatureEvent(hiveId, 37.5, "brood")); // +2.5°C

        // Insert sound events (rise >40%)
        ks.insert(new SoundEvent(hiveId, 60.0, 300.0));
        ks.insert(new SoundEvent(hiveId, 90.0, 350.0)); // +50% rise

        // Insert weight events (drop >1.5kg)
        ks.insert(new WeightEvent(hiveId, 42.0));
        ks.insert(new WeightEvent(hiveId, 39.5)); // -2.5kg

        int fired = ks.fireAllRules();
        System.out.println("CEP rules fired: " + fired);

        // Check if swarming alarm was generated
        boolean hasSwarmingAlarm = ks.getObjects().stream()
                .filter(o -> o instanceof Alarm)
                .anyMatch(o -> ((Alarm) o).getType().equals("SWARMING"));

        System.out.println("Swarming detected: " + hasSwarmingAlarm);

        // Print all alarms
        ks.getObjects().stream()
                .filter(o -> o instanceof Alarm)
                .forEach(o -> System.out.println("  ALARM: " + o));

        ks.dispose();
    }

    @Test
    public void testSuddenWeightDrop() {
        KieSession ks = kieContainer.newKieSession("cepSession");

        Long hiveId = 7L;

        // Weight drops from 45kg to 38kg (>5kg drop)
        ks.insert(new WeightEvent(hiveId, 45.0));
        ks.insert(new WeightEvent(hiveId, 38.0));

        ks.fireAllRules();

        boolean hasWeightAlarm = ks.getObjects().stream()
                .filter(o -> o instanceof Alarm)
                .anyMatch(o -> ((Alarm) o).getType().equals("WEIGHT_DROP"));

        assertTrue(hasWeightAlarm, "Should detect sudden weight drop of 7kg");

        ks.dispose();
    }
}
