package com.campingmanager.stays.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Esito del check-in: il soggiorno aggiornato e le credenziali generate per l'ospite.
 * La password temporanea va comunicata all'ospite (in Fase 3 verra inviata via email).
 */
@Data
@AllArgsConstructor
public class CheckInResponse {
    private SoggiornoDTO soggiorno;
    private String ospiteEmail;
    private String temporaryPassword;
}
