package com.beesmart.service.controllers;

import com.beesmart.service.services.SensorSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulator")
@CrossOrigin(origins = "http://localhost:4200")
public class SimulatorController {

    private final SensorSimulator simulator;

    @Autowired
    public SimulatorController(SensorSimulator simulator) {
        this.simulator = simulator;
    }

    /**
     * POST /api/simulator/start?scenario=SWARMING&hiveId=14
     * Available scenarios: NORMAL, SWARMING, ROBBING, QUEEN_LOSS, WEIGHT_DROP
     */
    @PostMapping("/start")
    public ResponseEntity<String> start(
            @RequestParam(defaultValue = "SWARMING") String scenario,
            @RequestParam(defaultValue = "14") Long hiveId) {
        String result = simulator.startScenario(scenario, hiveId);
        return ResponseEntity.ok(result);
    }

    /**
     * POST /api/simulator/stop
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stop() {
        String result = simulator.stopSimulation();
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/simulator/status
     */
    @GetMapping("/status")
    public ResponseEntity<SensorSimulator.SimulatorStatus> status() {
        return ResponseEntity.ok(simulator.getStatus());
    }
}
