package com.campingmanager.exceptions;

/**
 * Lanciata per richieste non valide a livello di logica applicativa. Mappata su HTTP 400.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
