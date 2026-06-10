package com.campingmanager.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BikeTypeCountDTO {
    private String type;
    private long count;
}
