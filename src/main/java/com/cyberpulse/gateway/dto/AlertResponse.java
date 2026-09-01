package com.cyberpulse.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertResponse {

    private boolean success;
    private String message;
    private String alertId;
    private String timestamp;

    public AlertResponse() {
    }

    public AlertResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AlertResponse(boolean success, String message, String alertId) {
        this.success = success;
        this.message = message;
        this.alertId = alertId;
    }

    public AlertResponse(boolean success, String message, String alertId, String timestamp) {
        this.success = success;
        this.message = message;
        this.alertId = alertId;
        this.timestamp = timestamp;
    }

    public static AlertResponse ok(String message) {
        return new AlertResponse(true, message);
    }

    public static AlertResponse ok(String message, String alertId) {
        return new AlertResponse(true, message, alertId);
    }

    public static AlertResponse error(String message) {
        return new AlertResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
