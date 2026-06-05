package com.campingmanager.exceptions;

/**
 * Lanciata quando un'operazione confligge con lo stato attuale
 * (es. email gia registrata, bici gia noleggiata). Mappata su HTTP 409.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
