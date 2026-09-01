package com.cyberpulse.gateway.controller;

import com.cyberpulse.gateway.dto.HealthResponse;
import com.cyberpulse.gateway.service.AlertForwardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final AlertForwardingService alertForwardingService;

    public HealthController(AlertForwardingService alertForwardingService) {
        this.alertForwardingService = alertForwardingService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        boolean mainReachable = alertForwardingService.isMainEngineReachable();
        String mainEngineStatus = mainReachable ? "ONLINE" : "OFFLINE";

        HealthResponse healthResponse = new HealthResponse(
                "UP",
                "ONLINE",
                mainEngineStatus,
                "CyberPulse Gateway is operating normally",
                Instant.now().toString()
        );

        return ResponseEntity.ok(healthResponse);
    }
}
