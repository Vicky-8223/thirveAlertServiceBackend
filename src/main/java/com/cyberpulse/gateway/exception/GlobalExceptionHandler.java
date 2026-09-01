package com.cyberpulse.gateway.exception;

import com.cyberpulse.gateway.dto.AlertResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AlertResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        if (errorMessage.isBlank()) {
            errorMessage = "Invalid alert data.";
        }

        logger.warn("Validation error on alert submission: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AlertResponse.error("Invalid alert data: " + errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AlertResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON payload: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AlertResponse.error("Invalid alert data."));
    }

    @ExceptionHandler(GatewayForwardingException.class)
    public ResponseEntity<AlertResponse> handleGatewayForwardingException(GatewayForwardingException ex) {
        logger.error("Gateway forwarding error: {}", ex.getMessage());
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        return ResponseEntity.status(status)
                .body(AlertResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<AlertResponse> handleResourceAccessException(ResourceAccessException ex) {
        logger.error("Downstream service connection failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(AlertResponse.error("Unable to reach the incident prioritization service."));
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<AlertResponse> handleRestClientResponseException(RestClientResponseException ex) {
        logger.error("Downstream service returned error code {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        return ResponseEntity.status(ex.getStatusCode())
                .body(AlertResponse.error("Downstream service error: " + ex.getStatusText()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AlertResponse> handleGeneralException(Exception ex) {
        logger.error("Unexpected error in gateway: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AlertResponse.error("Internal gateway error. Please try again."));
    }
}
