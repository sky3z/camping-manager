package com.campingmanager.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OccupancyDTO {
    private LocalDate from;
    private LocalDate to;
    private long occupied;
    private long total;
    private double occupancyRate; // percentuale
}
