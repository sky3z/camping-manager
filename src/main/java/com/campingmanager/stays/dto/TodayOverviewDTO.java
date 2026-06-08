package com.campingmanager.stays.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Quadro del giorno per la reception: arrivi e partenze previste.
 */
@Data
@AllArgsConstructor
public class TodayOverviewDTO {
    private LocalDate date;
    private List<SoggiornoDTO> arrivals;
    private List<SoggiornoDTO> departures;
}
