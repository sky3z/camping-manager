package com.campingmanager.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Email non valida")
    @NotBlank(message = "L'email e obbligatoria")
    private String email;

    @NotBlank(message = "La password e obbligatoria")
    private String password;
}
