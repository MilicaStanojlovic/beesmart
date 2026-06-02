package com.beesmart.service.controllers;
import com.beesmart.model.*;
import com.beesmart.service.services.TemplateService;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "http://localhost:4200")
public class TemplateController {

    private final TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * GET /api/templates/treatment?hiveType=LR
     * Get varroa treatment recommendations based on hive type using template rules.
     */
    @GetMapping("/treatment")
    public ResponseEntity<List<Recommendation>> getTreatmentByHiveType(
            @RequestParam String hiveType) {

        KieSession ks = templateService.createSessionFromTemplate(
                "/rules/templates/treatment-template.drt",
                "/rules/templates/treatment-data.csv"
        );

        // Insert facts
        Hive hive = new Hive(1L, hiveType, "Carniolan");
        ks.insert(hive);
        ks.insert(new Diagnosis("Varroosis", "URGENT", true));

        ks.fireAllRules();

        // Collect recommendations
        List<Recommendation> results = new ArrayList<>();
        for (Object obj : ks.getObjects()) {
            if (obj instanceof Recommendation) {
                results.add((Recommendation) obj);
            }
        }

        ks.dispose();
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/templates/seasonal?breed=Carniolan&month=5
     * Get seasonal activity recommendations based on breed and month.
     */
    @GetMapping("/seasonal")
    public ResponseEntity<List<Recommendation>> getSeasonalActivities(
            @RequestParam String breed,
            @RequestParam int month,
            @RequestParam(defaultValue = "15.0") double avgTemp) {

        KieSession ks = templateService.createSessionFromTemplate(
                "/rules/templates/seasonal-calendar-template.drt",
                "/rules/templates/seasonal-calendar-data.csv"
        );

        Hive hive = new Hive(1L, "LR", breed);
        ks.insert(hive);
        ks.insert(new Weather(month, avgTemp, avgTemp, avgTemp));

        ks.fireAllRules();

        List<Recommendation> results = new ArrayList<>();
        for (Object obj : ks.getObjects()) {
            if (obj instanceof Recommendation) {
                results.add((Recommendation) obj);
            }
        }

        ks.dispose();
        return ResponseEntity.ok(results);
    }
    /**
     * GET /api/templates/regional?region=Pannonian&month=5&avgTemp=18
     * Get regional activity recommendations based on climate zone.
     */
    @GetMapping("/regional")
    public ResponseEntity<List<Recommendation>> getRegionalActivities(
            @RequestParam String region,
            @RequestParam int month,
            @RequestParam(defaultValue = "15.0") double avgTemp) {

        KieSession ks = templateService.createSessionFromTemplate(
                "/rules/templates/climate-region-template.drt",
                "/rules/templates/climate-region-data.csv"
        );

        Hive hive = new Hive(1L, "LR", "Carniolan");
        hive.setRegion(region);
        ks.insert(hive);
        ks.insert(new Weather(month, avgTemp, avgTemp, avgTemp));

        ks.fireAllRules();

        List<Recommendation> results = new ArrayList<>();
        for (Object obj : ks.getObjects()) {
            if (obj instanceof Recommendation) {
                results.add((Recommendation) obj);
            }
        }

        ks.dispose();
        return ResponseEntity.ok(results);
    }
}
