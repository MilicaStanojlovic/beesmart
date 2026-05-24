package com.beesmart.service.controllers;


import com.beesmart.model.Alarm;
import com.beesmart.service.services.CepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cep")
@CrossOrigin(origins = "http://localhost:4200")
public class CepController {

    private final CepService cepService;

    @Autowired
    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    /**
     * POST /api/cep/temperature?hiveId=14&temperature=37.5&zone=brood
     * Insert temperature sensor reading. Returns any triggered alarms.
     */
    @PostMapping("/temperature")
    public ResponseEntity<List<Alarm>> insertTemperature(
            @RequestParam Long hiveId,
            @RequestParam double temperature,
            @RequestParam String zone) {
        List<Alarm> alarms = cepService.insertTemperature(hiveId, temperature, zone);
        return ResponseEntity.ok(alarms);
    }

    /**
     * POST /api/cep/sound?hiveId=14&decibels=85.0&frequency=450.0
     * Insert sound sensor reading.
     */
    @PostMapping("/sound")
    public ResponseEntity<List<Alarm>> insertSound(
            @RequestParam Long hiveId,
            @RequestParam double decibels,
            @RequestParam double frequency) {
        List<Alarm> alarms = cepService.insertSound(hiveId, decibels, frequency);
        return ResponseEntity.ok(alarms);
    }

    /**
     * POST /api/cep/weight?hiveId=14&weightKg=38.5
     * Insert weight sensor reading.
     */
    @PostMapping("/weight")
    public ResponseEntity<List<Alarm>> insertWeight(
            @RequestParam Long hiveId,
            @RequestParam double weightKg) {
        List<Alarm> alarms = cepService.insertWeight(hiveId, weightKg);
        return ResponseEntity.ok(alarms);
    }

    /**
     * POST /api/cep/humidity?hiveId=14&humidity=78.0
     * Insert humidity sensor reading.
     */
    @PostMapping("/humidity")
    public ResponseEntity<List<Alarm>> insertHumidity(
            @RequestParam Long hiveId,
            @RequestParam double humidity) {
        List<Alarm> alarms = cepService.insertHumidity(hiveId, humidity);
        return ResponseEntity.ok(alarms);
    }

    /**
     * GET /api/cep/alarms
     * Get all currently active alarms.
     */
    @GetMapping("/alarms")
    public ResponseEntity<List<Alarm>> getAlarms() {
        List<Alarm> alarms = cepService.getActiveAlarms();
        return ResponseEntity.ok(alarms);
    }

    /**
     * POST /api/cep/pasture?plant=acacia&active=false
     * Set pasture/bloom status (needed for robbing detection).
     */
    @PostMapping("/pasture")
    public ResponseEntity<String> setPasture(
            @RequestParam String plant,
            @RequestParam boolean active) {
        cepService.setPastureStatus(plant, active);
        return ResponseEntity.ok("Pasture status updated: " + plant + " = " + active);
    }
}
