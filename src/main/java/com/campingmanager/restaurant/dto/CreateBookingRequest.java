package com.campingmanager.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateBookingRequest {

    @NotNull(message = "L'id del tavolo e obbligatorio")
    private Long tableId;

    @NotNull(message = "La data e obbligatoria")
    private LocalDate bookingDate;

    @NotNull(message = "L'orario e obbligatorio")
    private LocalTime bookingTime;

    @Min(value = 1, message = "Deve esserci almeno una persona")
    private int numPeople;
}
