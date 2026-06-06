package com.campingmanager.accommodations.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccommodationRequest {

    @NotBlank(message = "Il nome e obbligatorio")
    private String name;

    private String description;

    @Min(value = 1, message = "La capacita deve essere almeno 1")
    private int maxCapacity;

    @NotNull(message = "Il prezzo per notte e obbligatorio")
    @DecimalMin(value = "0.0", message = "Il prezzo non puo essere negativo")
    private BigDecimal pricePerNight;

    @NotBlank(message = "Il tipo e obbligatorio")
    @Pattern(regexp = "CHALET|PIAZZOLA", message = "Il tipo deve essere CHALET o PIAZZOLA")
    private String type;

    // Specifici Chalet
    private Integer rooms;
    private Integer beds;
    private Boolean hasBathroom;
    private Boolean hasKitchen;
    private Boolean hasAc;

    // Specifici Piazzola
    @Pattern(regexp = "TENDA|CAMPER|CARAVAN", message = "Tipo piazzola non valido")
    private String tipoPiazzola;
    private Double surfaceM2;
    private Boolean hasElectricity;
    private Boolean hasWater;
}
