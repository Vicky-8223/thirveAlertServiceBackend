package com.cyberpulse.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AlertRequest {

    @NotBlank(message = "Timestamp is required")
    private String timestamp;

    @NotBlank(message = "Please select an alert source")
    @Size(max = 100, message = "Source name cannot exceed 100 characters")
    private String source;

    @NotBlank(message = "Alert title cannot be empty")
    @Size(max = 150, message = "Alert title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    public AlertRequest() {
    }

    public AlertRequest(String timestamp, String source, String title, String description) {
        this.timestamp = timestamp;
        this.source = source;
        this.title = title;
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AlertRequest{" +
                "timestamp='" + timestamp + '\'' +
                ", source='" + source + '\'' +
                ", title='" + title + '\'' +
                ", description='" + (description != null && description.length() > 50 ? description.substring(0, 50) + "..." : description) + '\'' +
                '}';
    }
}
