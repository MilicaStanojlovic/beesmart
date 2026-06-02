package com.beesmart.service.services;


import com.beesmart.model.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BackwardService {

    private final KieContainer kieContainer;

    @Autowired
    public BackwardService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    /**
     * Find all effects of a given cause using isCause recursive query.
     * Example: getAllEffects("Varroosis") → [DWV_virus, DeformedWings, WeakenedImmunity, CCD, ...]
     */
    public List<String> getAllEffects(String cause) {
        KieSession ks = kieContainer.newKieSession("forwardSession");
        List<String> effects = new ArrayList<>();

        try {
            insertCauseEffectFacts(ks);

            // Trigger the unbound query
            ks.insert("allEffectsOf_" + cause);

            // Use query directly
            org.kie.api.runtime.rule.QueryResults results =
                    ks.getQueryResults("isCause", cause, org.kie.api.runtime.rule.Variable.v);

            for (org.kie.api.runtime.rule.QueryResultsRow row : results) {
                String effect = (String) row.get("effect");
                //effects.add(effect);
                if (!effects.contains(effect)) {
                    effects.add(effect);
                }
            }

            return effects;
        } finally {
            ks.dispose();
        }
    }

    /**
     * Find all causes of a given effect using isCause recursive query.
     * Example: getAllCauses("DeformedWings") → [DWV_virus, Varroosis]
     */
    public List<String> getAllCauses(String effect) {
        KieSession ks = kieContainer.newKieSession("forwardSession");
        List<String> causes = new ArrayList<>();

        try {
            insertCauseEffectFacts(ks);

            org.kie.api.runtime.rule.QueryResults results =
                    ks.getQueryResults("isCause", org.kie.api.runtime.rule.Variable.v, effect);

            for (org.kie.api.runtime.rule.QueryResultsRow row : results) {
                String cause = (String) row.get("cause");
                //causes.add(cause);
                if (!causes.contains(cause)) {
                    causes.add(cause);
                }
            }

            return causes;
        } finally {
            ks.dispose();
        }
    }

    /**
     * Check if a specific cause-effect relationship exists.
     * Example: checkCauseEffect("Varroosis", "CCD") → true
     */
    public boolean checkCauseEffect(String cause, String effect) {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        try {
            insertCauseEffectFacts(ks);

            org.kie.api.runtime.rule.QueryResults results =
                    ks.getQueryResults("isCause", cause, effect);

            return results.size() > 0;
        } finally {
            ks.dispose();
        }
    }

    /**
     * Find all treatments in a given category using belongsToCategory query.
     * Example: getTreatmentsInCategory("AntiVarroaTreatment") → [OxalicAcid, FormicAcid, Amitraz, ...]
     */
    public List<String> getTreatmentsInCategory(String category) {
        KieSession ks = kieContainer.newKieSession("forwardSession");
        List<String> treatments = new ArrayList<>();

        try {
            insertTreatmentHierarchy(ks);

            org.kie.api.runtime.rule.QueryResults results =
                    ks.getQueryResults("belongsToCategory", org.kie.api.runtime.rule.Variable.v, category);

            for (org.kie.api.runtime.rule.QueryResultsRow row : results) {
                String item = (String) row.get("item");
                treatments.add(item);
            }

            return treatments;
        } finally {
            ks.dispose();
        }
    }

    /**
     * Get allowed treatments for organic production.
     * Returns anti-varroa treatments that are NOT chemical acaricides.
     */
    public List<String> getAllowedOrganicTreatments() {
        List<String> allAntiVarroa = getTreatmentsInCategory("AntiVarroaTreatment");
        List<String> chemical = getTreatmentsInCategory("ChemicalAcaricide");

        allAntiVarroa.removeAll(chemical);
        return allAntiVarroa;
    }

    // ─────────────────────────────────
    // Domain knowledge initialization
    // ─────────────────────────────────

    private void insertCauseEffectFacts(KieSession ks) {
        // Varroosis chain
        ks.insert(new Causes("Varroosis", "DWV_virus"));
        ks.insert(new Causes("DWV_virus", "DeformedWings"));
        ks.insert(new Causes("DWV_virus", "ShortenedLifespan"));
        ks.insert(new Causes("Varroosis", "WeakenedImmunity"));
        ks.insert(new Causes("WeakenedImmunity", "NosemaSusceptibility"));
        ks.insert(new Causes("WeakenedImmunity", "CCD"));
        ks.insert(new Causes("Varroosis", "ProteinLoss"));
        ks.insert(new Causes("ProteinLoss", "WeakWinterCluster"));

        // Pesticide chain
        ks.insert(new Causes("Pesticides", "WeakenedImmunity"));
        ks.insert(new Causes("Pesticides", "HoneyContamination"));
        ks.insert(new Causes("Pesticides", "BeeDisorientation"));
        ks.insert(new Causes("BeeDisorientation", "ForagerLoss"));
        ks.insert(new Causes("ForagerLoss", "CCD"));

        // Brood disease chain
        ks.insert(new Causes("AmericanFoulbrood", "BroodDecay"));
        ks.insert(new Causes("EuropeanFoulbrood", "BroodDecay"));
        ks.insert(new Causes("Chalkbrood", "BroodDecay"));
        ks.insert(new Causes("BroodDecay", "WeakColony"));
        ks.insert(new Causes("WeakColony", "RobbingSusceptibility"));

        // Environmental chain
        ks.insert(new Causes("HighHumidity", "Chalkbrood"));
        ks.insert(new Causes("LowTemperature", "BroodChilling"));
        ks.insert(new Causes("BroodChilling", "BroodDecay"));
        ks.insert(new Causes("OldQueen", "WeakEggLaying"));
        ks.insert(new Causes("WeakEggLaying", "WeakColony"));
    }

    private void insertTreatmentHierarchy(KieSession ks) {
        // Organic acids
        ks.insert(new BelongsToGroup("OxalicAcid", "OrganicAcids"));
        ks.insert(new BelongsToGroup("FormicAcid", "OrganicAcids"));
        ks.insert(new BelongsToGroup("OrganicAcids", "AntiVarroaTreatment"));

        // Chemical acaricides
        ks.insert(new BelongsToGroup("Amitraz", "ChemicalAcaricide"));
        ks.insert(new BelongsToGroup("Fluvalinate", "ChemicalAcaricide"));
        ks.insert(new BelongsToGroup("ChemicalAcaricide", "AntiVarroaTreatment"));

        // Top level
        ks.insert(new BelongsToGroup("AntiVarroaTreatment", "VeterinaryTreatment"));

        // Antibiotics
        ks.insert(new BelongsToGroup("Fumagillin", "Antibiotic"));
        ks.insert(new BelongsToGroup("Antibiotic", "VeterinaryTreatment"));
    }
}