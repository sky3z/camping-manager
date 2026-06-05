package com.campingmanager.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Corpo JSON standard restituito per ogni errore gestito dall'applicazione.
 */
@Getter
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
