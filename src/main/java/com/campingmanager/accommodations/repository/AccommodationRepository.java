package com.campingmanager.accommodations.repository;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findByStatus(AccommodationStatus status);

    boolean existsByName(String name);

    /**
     * Alloggi disponibili in un intervallo di date: non in manutenzione, con capacita
     * sufficiente e senza soggiorni (non cancellati) che si sovrappongono al periodo.
     */
    @Query("""
            SELECT a FROM Accommodation a
            WHERE a.status <> com.campingmanager.accommodations.entity.AccommodationStatus.MANUTENZIONE
              AND a.maxCapacity >= :capacity
              AND a.id NOT IN (
                  SELECT s.accommodation.id FROM Soggiorno s
                  WHERE s.status <> com.campingmanager.stays.entity.SoggiornoStatus.CANCELLATO
                    AND s.checkInDate < :checkOut
                    AND s.checkOutDate > :checkIn
              )
            ORDER BY a.pricePerNight ASC
            """)
    List<Accommodation> findAvailable(@Param("checkIn") LocalDate checkIn,
                                      @Param("checkOut") LocalDate checkOut,
                                      @Param("capacity") int capacity);
}
