package com.es.api_ciervus.error.exception;

public class ValidationException extends RuntimeException {

    private static final String DESCRIPTION = "Validation Error (400)";

    public ValidationException(String mensaje) {
        super(DESCRIPTION + ": " + mensaje);
    }
}
