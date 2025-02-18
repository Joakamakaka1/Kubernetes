package com.es.api_ciervus.error.exception;

public class ConflictException extends RuntimeException {
    private static final String DESCRIPCION = "Conflict Duplicate (409)";
    public ConflictException(String mensaje) {
        super(DESCRIPCION + ": " + mensaje);
    }
}
