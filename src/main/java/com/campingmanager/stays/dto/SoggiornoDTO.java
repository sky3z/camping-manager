package com.campingmanager.stays.dto;

import com.campingmanager.stays.entity.Soggiorno;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SoggiornoDTO {

    private Long id;
    private Long accommodationId;
    private String accommodationName;
    private String guestName;
    private String guestEmail;
    private int numGuests;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;
    private BigDecimal totalPrice;
    private String ospiteEmail;
    private LocalDateTime createdAt;

    public static SoggiornoDTO from(Soggiorno s) {
        SoggiornoDTO dto = new SoggiornoDTO();
        dto.setId(s.getId());
        if (s.getAccommodation() != null) {
            dto.setAccommodationId(s.getAccommodation().getId());
            dto.setAccommodationName(s.getAccommodation().getName());
        }
        dto.setGuestName(s.getGuestName());
        dto.setGuestEmail(s.getGuestEmail());
        dto.setNumGuests(s.getNumGuests());
        dto.setCheckInDate(s.getCheckInDate());
        dto.setCheckOutDate(s.getCheckOutDate());
        dto.setStatus(s.getStatus().name());
        dto.setTotalPrice(s.getTotalPrice());
        if (s.getOspiteAccount() != null) {
            dto.setOspiteEmail(s.getOspiteAccount().getEmail());
        }
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}
