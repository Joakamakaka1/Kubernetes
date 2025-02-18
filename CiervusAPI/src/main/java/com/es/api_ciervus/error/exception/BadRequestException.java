package com.es.api_ciervus.error.exception;

public class BadRequestException  extends RuntimeException{

    private static final String DESCRIPCION = "Bad Request (400)";
    public BadRequestException(String mensaje) {
        super(DESCRIPCION + ": " + mensaje);
    }
}