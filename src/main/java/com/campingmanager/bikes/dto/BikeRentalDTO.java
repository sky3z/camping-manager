package com.campingmanager.bikes.dto;

import com.campingmanager.bikes.entity.BikeRental;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BikeRentalDTO {

    private Long id;
    private Long bikeId;
    private String bikeCode;
    private String ospiteEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private String status;

    public static BikeRentalDTO from(BikeRental r) {
        BikeRentalDTO dto = new BikeRentalDTO();
        dto.setId(r.getId());
        if (r.getBike() != null) {
            dto.setBikeId(r.getBike().getId());
            dto.setBikeCode(r.getBike().getCode());
        }
        if (r.getOspite() != null) {
            dto.setOspiteEmail(r.getOspite().getEmail());
        }
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setTotalPrice(r.getTotalPrice());
        dto.setStatus(r.getStatus().name());
        return dto;
    }
}
