package com.campingmanager.accommodations.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Chalet in legno con stanze e servizi.
 */
@Entity
@Table(name = "chalet")
@DiscriminatorValue("CHALET")
@Getter
@Setter
@NoArgsConstructor
public class Chalet extends Accommodation {

    private int rooms;
    private int beds;
    private boolean hasBathroom;
    private boolean hasKitchen;
    private boolean hasAc;

    @Override
    public String getType() {
        return "CHALET";
    }
}
