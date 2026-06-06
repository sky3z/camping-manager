package com.campingmanager.accommodations.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Piazzola in erba per tenda, camper o caravan.
 */
@Entity
@Table(name = "piazzola")
@DiscriminatorValue("PIAZZOLA")
@Getter
@Setter
@NoArgsConstructor
public class Piazzola extends Accommodation {

    public enum TipoPiazzola {
        TENDA,
        CAMPER,
        CARAVAN
    }

    @Enumerated(EnumType.STRING)
    private TipoPiazzola tipoPiazzola;

    private Double surfaceM2;
    private boolean hasElectricity;
    private boolean hasWater;

    @Override
    public String getType() {
        return "PIAZZOLA";
    }
}
