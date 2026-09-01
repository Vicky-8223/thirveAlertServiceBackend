package com.cyberpulse.gateway.controller;

import com.cyberpulse.gateway.dto.AlertRequest;
import com.cyberpulse.gateway.dto.AlertResponse;
import com.cyberpulse.gateway.service.AlertForwardingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);
    private final AlertForwardingService alertForwardingService;

    public AlertController(AlertForwardingService alertForwardingService) {
        this.alertForwardingService = alertForwardingService;
    }

    @PostMapping
    public ResponseEntity<AlertResponse> submitAlert(@Valid @RequestBody AlertRequest alertRequest) {
        logger.info("Received incoming alert submission for source [{}] with title [{}]",
                alertRequest.getSource(), alertRequest.getTitle());

        AlertResponse response = alertForwardingService.forwardAlert(alertRequest);
        return ResponseEntity.ok(response);
    }
}
