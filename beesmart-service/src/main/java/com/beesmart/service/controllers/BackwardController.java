package com.beesmart.service.controllers;


import com.beesmart.service.services.BackwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backward")
@CrossOrigin(origins = "http://localhost:4200")
public class BackwardController {

    private final BackwardService backwardService;

    @Autowired
    public BackwardController(BackwardService backwardService) {
        this.backwardService = backwardService;
    }

    /**
     * GET /api/backward/effects?cause=Varroosis
     * Find all effects of a given cause (recursive).
     */
    @GetMapping("/effects")
    public ResponseEntity<Map<String, Object>> getAllEffects(@RequestParam String cause) {
        List<String> effects = backwardService.getAllEffects(cause);

        Map<String, Object> response = new HashMap<>();
        response.put("cause", cause);
        response.put("effects", effects);
        response.put("count", effects.size());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/backward/causes?effect=DeformedWings
     * Find all causes of a given effect (recursive).
     */
    @GetMapping("/causes")
    public ResponseEntity<Map<String, Object>> getAllCauses(@RequestParam String effect) {
        List<String> causes = backwardService.getAllCauses(effect);

        Map<String, Object> response = new HashMap<>();
        response.put("effect", effect);
        response.put("causes", causes);
        response.put("count", causes.size());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/backward/check?cause=Varroosis&effect=CCD
     * Check if a specific cause-effect chain exists.
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkCauseEffect(
            @RequestParam String cause, @RequestParam String effect) {
        boolean exists = backwardService.checkCauseEffect(cause, effect);

        Map<String, Object> response = new HashMap<>();
        response.put("cause", cause);
        response.put("effect", effect);
        response.put("confirmed", exists);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/backward/treatments?category=AntiVarroaTreatment
     * Find all treatments in a category (recursive hierarchy).
     */
    @GetMapping("/treatments")
    public ResponseEntity<Map<String, Object>> getTreatments(@RequestParam String category) {
        List<String> treatments = backwardService.getTreatmentsInCategory(category);

        Map<String, Object> response = new HashMap<>();
        response.put("category", category);
        response.put("treatments", treatments);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/backward/organic-treatments
     * Get all treatments allowed in organic production.
     */
    @GetMapping("/organic-treatments")
    public ResponseEntity<Map<String, Object>> getOrganicTreatments() {
        List<String> allowed = backwardService.getAllowedOrganicTreatments();

        Map<String, Object> response = new HashMap<>();
        response.put("allowedTreatments", allowed);
        response.put("note", "Chemical acaricides (Amitraz, Fluvalinate) excluded");

        return ResponseEntity.ok(response);
    }
}
