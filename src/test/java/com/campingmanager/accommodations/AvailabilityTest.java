package com.campingmanager.accommodations;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AvailabilityTest {

    @Autowired
    private AccommodationRepository accommodationRepository;
    @Autowired
    private SoggiornoRepository soggiornoRepository;

    private Chalet chalet(String name) {
        Chalet c = new Chalet();
        c.setName(name);
        c.setMaxCapacity(4);
        c.setPricePerNight(new BigDecimal("80.00"));
        return c;
    }

    @Test
    void shouldExcludeBookedAccommodationInOverlappingPeriod() {
        Chalet booked = (Chalet) accommodationRepository.save(chalet("Chalet 1"));
        Chalet free = (Chalet) accommodationRepository.save(chalet("Chalet 2"));

        Soggiorno s = new Soggiorno();
        s.setAccommodation(booked);
        s.setGuestName("Mario Rossi");
        s.setGuestEmail("mario@example.com");
        s.setNumGuests(2);
        s.setCheckInDate(LocalDate.of(2026, 7, 1));
        s.setCheckOutDate(LocalDate.of(2026, 7, 5));
        s.setStatus(SoggiornoStatus.PRENOTATO);
        soggiornoRepository.save(s);

        // periodo che si sovrappone: il bookato NON deve comparire
        List<Accommodation> overlap = accommodationRepository.findAvailable(
                LocalDate.of(2026, 7, 2), LocalDate.of(2026, 7, 4), 1);
        assertThat(overlap).extracting(Accommodation::getId).contains(free.getId());
        assertThat(overlap).extracting(Accommodation::getId).doesNotContain(booked.getId());

        // periodo libero: entrambi disponibili
        List<Accommodation> later = accommodationRepository.findAvailable(
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12), 1);
        assertThat(later).extracting(Accommodation::getId)
                .contains(booked.getId(), free.getId());
    }
}
