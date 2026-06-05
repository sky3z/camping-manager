package com.campingmanager.exceptions;

/**
 * Lanciata quando una risorsa richiesta non esiste. Mappata su HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
