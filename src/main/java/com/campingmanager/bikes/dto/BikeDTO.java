package com.campingmanager.bikes.dto;

import com.campingmanager.bikes.entity.Bike;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BikeDTO {

    private Long id;
    private String code;
    private String model;
    private String type;
    private BigDecimal pricePerDay;
    private String status;

    public static BikeDTO from(Bike b) {
        BikeDTO dto = new BikeDTO();
        dto.setId(b.getId());
        dto.setCode(b.getCode());
        dto.setModel(b.getModel());
        dto.setType(b.getType() == null ? null : b.getType().name());
        dto.setPricePerDay(b.getPricePerDay());
        dto.setStatus(b.getStatus().name());
        return dto;
    }
}
