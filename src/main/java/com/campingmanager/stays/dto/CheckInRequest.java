package com.campingmanager.stays.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * Dati anagrafici dell'ospite raccolti al check-in.
 */
@Data
public class CheckInRequest {

    @NotBlank(message = "Il numero del documento e obbligatorio")
    private String documentNumber;

    private String nationality;
    private String phone;
    private LocalDate birthDate;
}
