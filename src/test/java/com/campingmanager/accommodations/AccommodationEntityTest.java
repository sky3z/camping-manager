package com.campingmanager.accommodations;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.entity.Piazzola;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AccommodationEntityTest {

    @Autowired
    private AccommodationRepository repository;

    @Test
    void shouldPersistChalet() {
        Chalet chalet = new Chalet();
        chalet.setName("Chalet 1");
        chalet.setMaxCapacity(4);
        chalet.setPricePerNight(new BigDecimal("80.00"));
        chalet.setRooms(2);
        chalet.setBeds(4);
        chalet.setHasBathroom(true);

        Accommodation saved = repository.save(chalet);

        Accommodation found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found).isInstanceOf(Chalet.class);
        assertThat(found.getType()).isEqualTo("CHALET");
        assertThat(found.getStatus()).isEqualTo(AccommodationStatus.DISPONIBILE);
    }

    @Test
    void shouldPersistPiazzola() {
        Piazzola piazzola = new Piazzola();
        piazzola.setName("Piazzola 1");
        piazzola.setMaxCapacity(6);
        piazzola.setPricePerNight(new BigDecimal("25.00"));
        piazzola.setTipoPiazzola(Piazzola.TipoPiazzola.CAMPER);
        piazzola.setHasElectricity(true);

        Accommodation saved = repository.save(piazzola);

        Accommodation found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found).isInstanceOf(Piazzola.class);
        assertThat(found.getType()).isEqualTo("PIAZZOLA");
        assertThat(((Piazzola) found).getTipoPiazzola()).isEqualTo(Piazzola.TipoPiazzola.CAMPER);
    }
}
