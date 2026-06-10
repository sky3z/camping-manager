package com.campingmanager.stats;

import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.payments.repository.PaymentRepository;
import com.campingmanager.restaurant.repository.TableBookingRepository;
import com.campingmanager.stays.repository.SoggiornoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Verifica che le query aggregate delle statistiche vengano tradotte ed eseguite
 * correttamente dal database (anche a tabelle vuote), evitando errori di sintassi/funzioni SQL.
 */
@DataJpaTest
@ActiveProfiles("test")
class StatsQueriesTest {

    @Autowired
    private SoggiornoRepository soggiornoRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BikeRentalRepository rentalRepository;
    @Autowired
    private TableBookingRepository bookingRepository;

    @Test
    void aggregateQueriesShouldExecute() {
        assertThatCode(() -> {
            soggiornoRepository.countOccupied(LocalDate.now(), LocalDate.now().plusDays(1));
            paymentRepository.revenueByMonth();
            rentalRepository.countByBikeType();
            bookingRepository.countByNight();
        }).doesNotThrowAnyException();
    }
}
