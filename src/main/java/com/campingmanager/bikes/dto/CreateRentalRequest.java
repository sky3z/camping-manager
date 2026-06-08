package com.campingmanager.bikes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRentalRequest {

    @NotNull(message = "L'id della bici e obbligatorio")
    private Long bikeId;

    @NotNull(message = "La data di inizio e obbligatoria")
    private LocalDate startDate;

    @NotNull(message = "La data di fine e obbligatoria")
    private LocalDate endDate;
}
