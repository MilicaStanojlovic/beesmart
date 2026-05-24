package com.beesmart.service.controllers;


import com.beesmart.model.*;
import com.beesmart.service.services.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diagnosis")
@CrossOrigin(origins = "http://localhost:4200")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @Autowired
    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    /**
     * POST /api/diagnosis/run
     * Run full forward chaining diagnosis.
     * Accepts hive data, weather, symptoms, and returns diagnoses + recommendations.
     *
     * Example request body:
     * {
     *   "hive": { "id": 14, "hiveType": "LR", "breed": "Carniolan", "frameCount": 8, ... },
     *   "weather": { "month": 8, "avgWeeklyTemp": 25.0, "currentTemp": 28.0, ... },
     *   "blooms": [ { "plant": "acacia", "active": false } ],
     *   "symptoms": [ { "name": "deformed_wings" }, { "name": "visible_mites" } ],
     *   "miteDropCount": 18
     * }
     */
    @PostMapping("/run")
    public ResponseEntity<DiagnosisResult> runDiagnosis(@RequestBody DiagnosisRequest request) {
        DiagnosisResult result = diagnosisService.runDiagnosis(request);
        return ResponseEntity.ok(result);
    }
}