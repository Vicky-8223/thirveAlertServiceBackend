package com.cyberpulse.gateway.service;

import com.cyberpulse.gateway.dto.AlertRequest;
import com.cyberpulse.gateway.dto.AlertResponse;
import com.cyberpulse.gateway.exception.GatewayForwardingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Service
public class AlertForwardingService {

    private static final Logger logger = LoggerFactory.getLogger(AlertForwardingService.class);

    private final RestClient restClient;
    private final String mainApplicationUrl;

    public AlertForwardingService(
            RestClient restClient,
            @Value("${main.application.url:http://localhost:8081}") String mainApplicationUrl) {
        this.restClient = restClient;
        // Trim trailing slashes if present
        this.mainApplicationUrl = mainApplicationUrl.endsWith("/")
                ? mainApplicationUrl.substring(0, mainApplicationUrl.length() - 1)
                : mainApplicationUrl;
    }

    public String getMainApplicationUrl() {
        return mainApplicationUrl;
    }

    /**
     * Forwards the alert to the main full-stack application API endpoint:
     * POST ${MAIN_APPLICATION_URL}/api/alerts
     */
    public AlertResponse forwardAlert(AlertRequest alertRequest) {
        String targetUrl = mainApplicationUrl + "/api/alerts";
        logger.info("Forwarding alert from source [{}] to downstream target: {}", alertRequest.getSource(), targetUrl);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restClient.post()
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(alertRequest)
                    .retrieve()
                    .toEntity(Map.class);

            Map<?, ?> body = response.getBody();
            logger.info("Downstream response status: {}", response.getStatusCode());

            String message = "Alert forwarded successfully";
            String alertId = null;

            if (body != null) {
                if (body.containsKey("message") && body.get("message") != null) {
                    message = body.get("message").toString();
                }
                if (body.containsKey("alertId") && body.get("alertId") != null) {
                    alertId = body.get("alertId").toString();
                } else if (body.containsKey("id") && body.get("id") != null) {
                    alertId = body.get("id").toString();
                }
            }

            return AlertResponse.ok(message, alertId);

        } catch (ResourceAccessException ex) {
            logger.error("Unable to reach downstream service at {}: {}", targetUrl, ex.getMessage());
            throw new GatewayForwardingException("Unable to reach the incident prioritization service.", 503);
        } catch (RestClientResponseException ex) {
            logger.error("Downstream service returned error [{}]: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new GatewayForwardingException("Downstream engine returned error: " + ex.getStatusText(), ex.getStatusCode().value());
        } catch (Exception ex) {
            logger.error("Unexpected error forwarding alert: ", ex);
            throw new GatewayForwardingException("Unable to reach the incident prioritization service.", 503);
        }
    }

    /**
     * Checks if the downstream main application is reachable.
     */
    public boolean isMainEngineReachable() {
        String healthUrl = mainApplicationUrl + "/api/health";
        try {
            ResponseEntity<String> response = restClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .toEntity(String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            logger.debug("Downstream health check failed at {}: {}", healthUrl, ex.getMessage());
            return false;
        }
    }
}
