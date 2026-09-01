package com.cyberpulse.gateway;

import com.cyberpulse.gateway.dto.AlertRequest;
import com.cyberpulse.gateway.dto.AlertResponse;
import com.cyberpulse.gateway.exception.GatewayForwardingException;
import com.cyberpulse.gateway.service.AlertForwardingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CyberPulseGatewayApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertForwardingService alertForwardingService;

    @Test
    @DisplayName("Health endpoint returns status UP and main engine state")
    void testHealthEndpoint() throws Exception {
        when(alertForwardingService.isMainEngineReachable()).thenReturn(true);

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.gateway").value("ONLINE"))
                .andExpect(jsonPath("$.mainEngine").value("ONLINE"));
    }

    @Test
    @DisplayName("POST /api/alerts succeeds with valid payload and downstream response")
    void testSubmitAlertSuccess() throws Exception {
        AlertRequest request = new AlertRequest(
                "2026-09-01T10:42:15Z",
                "Microsoft Defender",
                "Suspicious PowerShell Execution",
                "PowerShell executed an encoded command on a finance endpoint."
        );

        when(alertForwardingService.forwardAlert(any(AlertRequest.class)))
                .thenReturn(AlertResponse.ok("Alert forwarded successfully", "ALT-1042"));

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Alert forwarded successfully"))
                .andExpect(jsonPath("$.alertId").value("ALT-1042"));
    }

    @Test
    @DisplayName("POST /api/alerts rejects blank required fields with 400 Bad Request")
    void testSubmitAlertValidationFailure() throws Exception {
        AlertRequest invalidRequest = new AlertRequest(
                "",
                "",
                "",
                ""
        );

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/alerts returns 503 when downstream service is unreachable")
    void testSubmitAlertDownstreamOffline() throws Exception {
        AlertRequest request = new AlertRequest(
                "2026-09-01T10:42:15Z",
                "CrowdStrike",
                "Suspicious LSASS Dump",
                "LSASS process memory was dumped by unauthorized tool."
        );

        when(alertForwardingService.forwardAlert(any(AlertRequest.class)))
                .thenThrow(new GatewayForwardingException("Unable to reach the incident prioritization service.", 503));

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unable to reach the incident prioritization service."));
    }
}
