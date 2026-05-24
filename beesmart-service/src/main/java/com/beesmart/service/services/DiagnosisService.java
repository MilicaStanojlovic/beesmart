package com.beesmart.service.services;

import com.beesmart.model.*;
import com.beesmart.model.events.MiteDropEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiagnosisService {

    private final KieContainer kieContainer;

    @Autowired
    public DiagnosisService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    /**
     * Run full forward chaining diagnosis.
     * Level 1: Season + Symptom markers
     * Level 2: Hive condition + Disease diagnosis
     * Level 3: Recommendations + Treatments
     * Level 4: Validation + Warnings
     */
    public DiagnosisResult runDiagnosis(DiagnosisRequest request) {
        KieSession ks = kieContainer.newKieSession("forwardSession");

        try {
            // Insert hive
            Hive hive = request.getHive();
            ks.insert(hive);

            // Insert weather
            if (request.getWeather() != null) {
                ks.insert(request.getWeather());
            }

            // Insert blooms
            if (request.getBlooms() != null) {
                for (Bloom bloom : request.getBlooms()) {
                    ks.insert(bloom);
                }
            }

            // Insert symptoms
            if (request.getSymptoms() != null) {
                for (Symptom symptom : request.getSymptoms()) {
                    ks.insert(symptom);
                }
            }

            // Insert mite drop if present
            if (request.getMiteDropCount() > 0) {
                ks.insert(new MiteDropEvent(request.getMiteDropCount()));
            }

            // Insert treatment history if present
            if (request.getTreatmentHistory() != null) {
                for (TreatmentHistory th : request.getTreatmentHistory()) {
                    ks.insert(th);
                }
            }

            // Fire all rules (4 levels chain automatically)
            int rulesFired = ks.fireAllRules();

            // Collect results from working memory
            DiagnosisResult result = new DiagnosisResult();
            result.setRulesFired(rulesFired);

            for (Object obj : ks.getObjects()) {
                if (obj instanceof SeasonPhase) {
                    result.setSeasonPhase(((SeasonPhase) obj).getName());
                } else if (obj instanceof SocietyStatus) {
                    result.getConditions().add((SocietyStatus) obj);
                } else if (obj instanceof Diagnosis) {
                    result.getDiagnoses().add((Diagnosis) obj);
                } else if (obj instanceof Recommendation) {
                    result.getRecommendations().add((Recommendation) obj);
                } else if (obj instanceof Alarm) {
                    result.getAlarms().add((Alarm) obj);
                }
            }

            return result;
        } finally {
            ks.dispose();
        }
    }
}