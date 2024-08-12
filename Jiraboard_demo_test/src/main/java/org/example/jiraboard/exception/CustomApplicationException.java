package org.example.jiraboard.exception;

public class CustomApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public CustomApplicationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
