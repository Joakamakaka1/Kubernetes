package com.es.api_ciervus.error.exception;

public class ForbiddenException extends RuntimeException {
    private static final String DESCRIPCION = "Forbidden (403)";

    public ForbiddenException(String mensaje) {
        super(DESCRIPCION + ": " + mensaje);
    }
}

