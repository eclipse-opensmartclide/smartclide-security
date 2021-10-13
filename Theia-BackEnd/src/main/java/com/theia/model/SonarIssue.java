package com.theia.model;

public class SonarIssue {
    private String severity;
    private String line;
    private String message;
    private String path;

    public SonarIssue(String severity, String line, String message, String path) {
        this.severity = severity;
        this.line = line;
        this.message = message;
        this.path = path;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
