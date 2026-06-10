package com.campingmanager.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BusyNightDTO {
    private LocalDate date;
    private long count;
}
