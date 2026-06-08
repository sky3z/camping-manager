package com.campingmanager.restaurant.dto;

import com.campingmanager.restaurant.entity.TableBooking;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TableBookingDTO {

    private Long id;
    private Long tableId;
    private int tableNumber;
    private String ospiteEmail;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private int numPeople;
    private String status;

    public static TableBookingDTO from(TableBooking b) {
        TableBookingDTO dto = new TableBookingDTO();
        dto.setId(b.getId());
        if (b.getTable() != null) {
            dto.setTableId(b.getTable().getId());
            dto.setTableNumber(b.getTable().getNumber());
        }
        if (b.getOspite() != null) {
            dto.setOspiteEmail(b.getOspite().getEmail());
        }
        dto.setBookingDate(b.getBookingDate());
        dto.setBookingTime(b.getBookingTime());
        dto.setNumPeople(b.getNumPeople());
        dto.setStatus(b.getStatus().name());
        return dto;
    }
}
