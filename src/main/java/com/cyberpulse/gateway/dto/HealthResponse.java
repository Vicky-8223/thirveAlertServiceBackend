package com.cyberpulse.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthResponse {

    private String status;
    private String gateway;
    private String mainEngine;
    private String message;
    private String timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status, String gateway, String mainEngine, String message, String timestamp) {
        this.status = status;
        this.gateway = gateway;
        this.mainEngine = mainEngine;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getMainEngine() {
        return mainEngine;
    }

    public void setMainEngine(String mainEngine) {
        this.mainEngine = mainEngine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
