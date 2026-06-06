package com.campingmanager.stays;

import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import com.campingmanager.users.entity.Ospite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SoggiornoEntityTest {

    @Autowired
    private SoggiornoRepository soggiornoRepository;
    @Autowired
    private AccommodationRepository accommodationRepository;

    @Test
    void shouldPersistSoggiornoLinkedToAccommodation() {
        Chalet chalet = new Chalet();
        chalet.setName("Chalet 1");
        chalet.setMaxCapacity(4);
        chalet.setPricePerNight(new BigDecimal("80.00"));
        Chalet savedChalet = accommodationRepository.save(chalet);

        Soggiorno soggiorno = new Soggiorno();
        soggiorno.setAccommodation(savedChalet);
        soggiorno.setGuestName("Mario Rossi");
        soggiorno.setGuestEmail("mario@example.com");
        soggiorno.setNumGuests(2);
        soggiorno.setCheckInDate(LocalDate.of(2026, 7, 1));
        soggiorno.setCheckOutDate(LocalDate.of(2026, 7, 5));
        soggiorno.setTotalPrice(new BigDecimal("320.00"));

        Soggiorno saved = soggiornoRepository.save(soggiorno);

        Soggiorno found = soggiornoRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getAccommodation().getId()).isEqualTo(savedChalet.getId());
        assertThat(found.getStatus()).isEqualTo(SoggiornoStatus.PRENOTATO);
        assertThat(found.getGuestName()).isEqualTo("Mario Rossi");
    }

    @Test
    void ospiteShouldBeDisabledAfterValidityDate() {
        Ospite ospite = new Ospite();
        ospite.setAccountValidUntil(LocalDate.now().minusDays(1));
        assertThat(ospite.isEnabled()).isFalse();

        ospite.setAccountValidUntil(LocalDate.now().plusDays(1));
        assertThat(ospite.isEnabled()).isTrue();
    }
}
