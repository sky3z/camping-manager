package com.campingmanager.restaurant.dto;

import com.campingmanager.restaurant.entity.RestaurantTable;
import lombok.Data;

@Data
public class TableDTO {

    private Long id;
    private int number;
    private int seats;
    private String location;

    public static TableDTO from(RestaurantTable t) {
        TableDTO dto = new TableDTO();
        dto.setId(t.getId());
        dto.setNumber(t.getNumber());
        dto.setSeats(t.getSeats());
        dto.setLocation(t.getLocation().name());
        return dto;
    }
}
