package com.campingmanager.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Risposta di autenticazione: token JWT + dati essenziali dell'utente.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
}
