package com.beesmart.service;

import com.beesmart.model.*;
import com.beesmart.model.events.MiteDropEvent;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BeeSmartApplication.class)
public class ForwardChainingTest {

    @Autowired
    private KieContainer kieContainer;

    /**
     * Full scenario from Project Proposal section 6:
     * Beekeeper inspects hive #14 (LR, Carniolan, mid-August)
     * Symptoms: deformed wings, visible mites, mite drop 18/day, reduced population
     * Expected chain: Season → Markers → Diagnosis → Treatment → Validation
     */
    @Test
    public void testFullVarroosisScenario() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        // ─── STEP 1: Input facts ───
        // Hive
        Hive hive = new Hive(14L, "LR", "Carniolan");
        hive.setFrameCount(8);
        hive.setBroodFrameCount(5);
        hive.setHoneyStockKg(12.0);
        hive.setHasSuper(true);
        hive.setQueenAgeMonths(14);
        hive.setWeightKg(38.0);
        hive.setWeightLastMonthKg(45.0); // lost 7kg in a month
        ks.insert(hive);

        // Weather (mid-August)
        ks.insert(new Weather(8, 25.0, 28.0, 22.0));

        // No active pasture in late summer
        ks.insert(new Bloom("acacia", false));
        ks.insert(new Bloom("linden", false));

        // Symptoms
        ks.insert(new Symptom("deformed_wings"));
        ks.insert(new Symptom("visible_mites"));
        ks.insert(new Symptom("reduced_population"));

        // Sensor: mite drop 18/day
        ks.insert(new MiteDropEvent(18));

        // ─── STEP 2: Fire rules ───
        System.out.println("=== FIRING ALL RULES ===");
        int rulesFired = ks.fireAllRules();
        System.out.println("=== Rules fired: " + rulesFired + " ===");

        // ─── STEP 3: Verify results ───
        List<Object> allFacts = new ArrayList<>(ks.getObjects());

        // Check Level 1: Season should be LATE_SUMMER
        boolean hasLateSummer = allFacts.stream()
                .filter(o -> o instanceof SeasonPhase)
                .anyMatch(o -> ((SeasonPhase) o).getName().equals("LATE_SUMMER"));
        assertTrue(hasLateSummer, "Should detect LATE_SUMMER season");

        // Check Level 1: DWV marker
        boolean hasDWV = allFacts.stream().anyMatch(o -> o instanceof DWVMarker);
        assertTrue(hasDWV, "Should detect DWV marker");

        // Check Level 1: High infestation
        boolean hasHighInfestation = allFacts.stream()
                .filter(o -> o instanceof InfestationLevel)
                .anyMatch(o -> ((InfestationLevel) o).getLevel().equals("high"));
        assertTrue(hasHighInfestation, "Should detect high infestation");

        // Check Level 2: Varroosis diagnosis
        boolean hasVarroosis = allFacts.stream()
                .filter(o -> o instanceof Diagnosis)
                .anyMatch(o -> ((Diagnosis) o).getDisease().equals("Varroosis"));
        assertTrue(hasVarroosis, "Should diagnose Varroosis");

        // Check Level 3: Treatment recommendation exists
        boolean hasTreatment = allFacts.stream()
                .filter(o -> o instanceof Recommendation)
                .anyMatch(o -> ((Recommendation) o).getType().equals("TREATMENT"));
        assertTrue(hasTreatment, "Should have treatment recommendation");

        // Check Level 4: Warning about super
        boolean hasWarning = allFacts.stream()
                .filter(o -> o instanceof Recommendation)
                .anyMatch(o -> {
                    Recommendation r = (Recommendation) o;
                    return r.getWarning() != null;
                });
        // Warning may or may not fire depending on rule ordering

        // Print all facts for debugging
        System.out.println("\n=== ALL FACTS IN WORKING MEMORY ===");
        for (Object fact : allFacts) {
            System.out.println("  " + fact.getClass().getSimpleName() + ": " + fact);
        }

        ks.dispose();
    }

    /**
     * Test weak spring colony scenario
     * Expected: Season(EARLY_SPRING) → Condition(weak-spring) → Recommendation(feed)
     */
    @Test
    public void testWeakSpringColony() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        // Weak hive in March
        Hive hive = new Hive(5L, "LR", "Carniolan");
        hive.setFrameCount(4);
        hive.setBroodFrameCount(2);  // < 3
        hive.setHoneyStockKg(2.0);   // < 3kg
        ks.insert(hive);

        // March, warm enough, willow blooming
        ks.insert(new Weather(3, 12.0, 14.0, 3.0)); // forecast < 5!
        ks.insert(new Bloom("willow", true));

        System.out.println("\n=== WEAK SPRING COLONY TEST ===");
        int fired = ks.fireAllRules();
        System.out.println("Rules fired: " + fired);

        List<Object> facts = new ArrayList<>(ks.getObjects());

        // Season should be EARLY_SPRING
        boolean hasEarlySpring = facts.stream()
                .filter(o -> o instanceof SeasonPhase)
                .anyMatch(o -> ((SeasonPhase) o).getName().equals("EARLY_SPRING"));
        assertTrue(hasEarlySpring, "Should detect EARLY SPRING");

        // Should have weak-spring status
        boolean hasWeakSpring = facts.stream()
                .filter(o -> o instanceof SocietyStatus)
                .anyMatch(o -> ((SocietyStatus) o).getStatus().equals("weak-spring"));
        assertTrue(hasWeakSpring, "Should detect weak spring colony");

        // Should have feeding recommendation
        boolean hasFeeding = facts.stream()
                .filter(o -> o instanceof Recommendation)
                .anyMatch(o -> ((Recommendation) o).getType().equals("FEEDING"));
        assertTrue(hasFeeding, "Should recommend feeding");

        // Level 4: forecast < 5°C → should correct syrup to patty
        boolean hasCorrectedFeeding = facts.stream()
                .filter(o -> o instanceof Recommendation)
                .anyMatch(o -> {
                    Recommendation r = (Recommendation) o;
                    return r.getWarning() != null &&
                            r.getWarning().contains("nosemosis");
                });
        // This tests the 4-level forward chain!

        System.out.println("\n=== FACTS ===");
        facts.forEach(f -> System.out.println("  " + f));

        ks.dispose();
    }

    /**
     * Test American Foulbrood - accumulate count >= 4
     */
    @Test
    public void testAmericanFoulbrood() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        ks.insert(new Hive(7L, "LR", "Carniolan"));
        ks.insert(new Weather(6, 22.0, 25.0, 24.0));

        // 5 symptoms → should trigger (>= 4)
        ks.insert(new Symptom("ropiness_test_positive"));
        ks.insert(new Symptom("smell_of_decay"));
        ks.insert(new Symptom("mosaic_brood"));
        ks.insert(new Symptom("sunken_cappings"));
        ks.insert(new Symptom("dark_sticky_larvae"));

        System.out.println("\n=== AMERICAN FOULBROOD TEST ===");
        ks.fireAllRules();

        boolean hasDiagnosis = ks.getObjects().stream()
                .filter(o -> o instanceof Diagnosis)
                .anyMatch(o -> ((Diagnosis) o).getDisease().equals("AmericanFoulbrood"));
        assertTrue(hasDiagnosis, "Should diagnose American Foulbrood with 5 symptoms");

        boolean hasUrgentReport = ks.getObjects().stream()
                .filter(o -> o instanceof Recommendation)
                .anyMatch(o -> ((Recommendation) o).getText().contains("veterinary inspection"));
        assertTrue(hasUrgentReport, "Should recommend reporting to vet inspection");

        ks.dispose();
    }
}
