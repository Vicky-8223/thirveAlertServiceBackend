package com.cyberpulse.gateway.exception;

public class GatewayForwardingException extends RuntimeException {

    private final int statusCode;

    public GatewayForwardingException(String message) {
        super(message);
        this.statusCode = 503;
    }

    public GatewayForwardingException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GatewayForwardingException(String message, Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
