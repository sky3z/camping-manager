package com.campingmanager.accommodations.dto;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.entity.Piazzola;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Vista di un alloggio. Include i campi comuni piu quelli specifici del tipo concreto.
 */
@Data
public class AccommodationDTO {

    private Long id;
    private String name;
    private String description;
    private int maxCapacity;
    private BigDecimal pricePerNight;
    private String status;
    private String type;

    // Specifici Chalet
    private Integer rooms;
    private Integer beds;
    private Boolean hasBathroom;
    private Boolean hasKitchen;
    private Boolean hasAc;

    // Specifici Piazzola
    private String tipoPiazzola;
    private Double surfaceM2;
    private Boolean hasElectricity;
    private Boolean hasWater;

    public static AccommodationDTO from(Accommodation a) {
        AccommodationDTO dto = new AccommodationDTO();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setDescription(a.getDescription());
        dto.setMaxCapacity(a.getMaxCapacity());
        dto.setPricePerNight(a.getPricePerNight());
        dto.setStatus(a.getStatus().name());
        dto.setType(a.getType());

        if (a instanceof Chalet c) {
            dto.setRooms(c.getRooms());
            dto.setBeds(c.getBeds());
            dto.setHasBathroom(c.isHasBathroom());
            dto.setHasKitchen(c.isHasKitchen());
            dto.setHasAc(c.isHasAc());
        } else if (a instanceof Piazzola p) {
            dto.setTipoPiazzola(p.getTipoPiazzola() == null ? null : p.getTipoPiazzola().name());
            dto.setSurfaceM2(p.getSurfaceM2());
            dto.setHasElectricity(p.isHasElectricity());
            dto.setHasWater(p.isHasWater());
        }
        return dto;
    }
}
