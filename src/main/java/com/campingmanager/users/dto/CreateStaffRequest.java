package com.campingmanager.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateStaffRequest {

    @Email(message = "Email non valida")
    @NotBlank(message = "L'email e obbligatoria")
    private String email;

    @NotBlank(message = "La password e obbligatoria")
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    private String password;

    @NotBlank(message = "Il nome e obbligatorio")
    private String name;

    @NotBlank(message = "Il cognome e obbligatorio")
    private String surname;

    @Pattern(regexp = "RECEPTION|RISTORANTE|MANUTENZIONE",
            message = "Il reparto deve essere RECEPTION, RISTORANTE o MANUTENZIONE")
    private String department;
}
