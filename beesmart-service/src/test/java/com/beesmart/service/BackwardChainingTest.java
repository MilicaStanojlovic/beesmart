package com.beesmart.service;

import com.beesmart.model.*;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = BeeSmartApplication.class)
public class BackwardChainingTest {
    @Autowired
    private KieContainer kieContainer;

    @Test
    public void testIsCauseQuery_Bound() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        // Insert cause-effect facts
        ks.insert(new Causes("Varroosis", "DWV_virus"));
        ks.insert(new Causes("DWV_virus", "DeformedWings"));
        ks.insert(new Causes("DWV_virus", "ShortenedLifespan"));
        ks.insert(new Causes("Varroosis", "WeakenedImmunity"));
        ks.insert(new Causes("WeakenedImmunity", "NosemaSusceptibility"));
        ks.insert(new Causes("WeakenedImmunity", "CCD"));
        ks.insert(new Causes("Pesticides", "WeakenedImmunity"));
        ks.insert(new Causes("Pesticides", "HoneyContamination"));

        // Trigger: check if Varroosis causes DeformedWings
        ks.insert("checkVarroaWings");

        System.out.println("=== BACKWARD: Bound query test ===");
        int fired = ks.fireAllRules();
        System.out.println("Rules fired: " + fired);
        assertTrue(fired > 0, "Should confirm Varroosis causes DeformedWings");

        ks.dispose();
    }

    @Test
    public void testIsCauseQuery_Unbound_AllEffects() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        ks.insert(new Causes("Varroosis", "DWV_virus"));
        ks.insert(new Causes("DWV_virus", "DeformedWings"));
        ks.insert(new Causes("DWV_virus", "ShortenedLifespan"));
        ks.insert(new Causes("Varroosis", "WeakenedImmunity"));
        ks.insert(new Causes("WeakenedImmunity", "NosemaSusceptibility"));
        ks.insert(new Causes("WeakenedImmunity", "CCD"));

        // Trigger: find ALL effects of Varroosis
        ks.insert("allVarroaEffects");

        System.out.println("\n=== BACKWARD: Unbound query - all effects ===");
        int fired = ks.fireAllRules();
        System.out.println("Rules fired: " + fired);
        // Should print: DWV_virus, DeformedWings, ShortenedLifespan,
        //               WeakenedImmunity, NosemaSusceptibility, CCD
        assertTrue(fired >= 6, "Should find at least 6 effects of Varroosis");

        ks.dispose();
    }

    @Test
    public void testBelongsToCategory_OrganicBan() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        // Treatment hierarchy
        ks.insert(new BelongsToGroup("OxalicAcid", "OrganicAcids"));
        ks.insert(new BelongsToGroup("FormicAcid", "OrganicAcids"));
        ks.insert(new BelongsToGroup("OrganicAcids", "AntiVarroaTreatment"));
        ks.insert(new BelongsToGroup("Amitraz", "ChemicalAcaricide"));
        ks.insert(new BelongsToGroup("ChemicalAcaricide", "AntiVarroaTreatment"));
        ks.insert(new BelongsToGroup("AntiVarroaTreatment", "VeterinaryTreatment"));

        // Organic hive
        Hive hive = new Hive(1L, "LR", "Carniolan");
        hive.setOrganicProduction(true);
        ks.insert(hive);

        // Trigger
        ks.insert("checkOrganic");

        System.out.println("\n=== BACKWARD: Organic ban check ===");
        int fired = ks.fireAllRules();
        System.out.println("Rules fired: " + fired);

        ks.dispose();
    }

    @Test
    public void testBelongsToCategory_AllowedOrganic() {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        ks.insert(new BelongsToGroup("OxalicAcid", "OrganicAcids"));
        ks.insert(new BelongsToGroup("FormicAcid", "OrganicAcids"));
        ks.insert(new BelongsToGroup("OrganicAcids", "AntiVarroaTreatment"));
        ks.insert(new BelongsToGroup("Amitraz", "ChemicalAcaricide"));
        ks.insert(new BelongsToGroup("ChemicalAcaricide", "AntiVarroaTreatment"));

        ks.insert("allowedOrganic");

        System.out.println("\n=== BACKWARD: Allowed organic treatments ===");
        int fired = ks.fireAllRules();
        // Should print: OxalicAcid, FormicAcid, OrganicAcids
        System.out.println("Rules fired: " + fired);

        ks.dispose();
    }
}
