package com.campingmanager.bikes.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBikeRequest {

    @NotBlank(message = "Il codice e obbligatorio")
    private String code;

    private String model;

    @NotBlank(message = "Il tipo e obbligatorio")
    @Pattern(regexp = "CITY|MTB|EBIKE|BAMBINO", message = "Tipo bici non valido")
    private String type;

    @NotNull(message = "Il prezzo giornaliero e obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo non puo essere negativo")
    private BigDecimal pricePerDay;
}
