package com.campingmanager.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateTableRequest {

    @NotNull(message = "Il numero del tavolo e obbligatorio")
    private Integer number;

    @Min(value = 1, message = "Il tavolo deve avere almeno un posto")
    private int seats;

    @Pattern(regexp = "INTERNO|ESTERNO", message = "La posizione deve essere INTERNO o ESTERNO")
    private String location;
}
