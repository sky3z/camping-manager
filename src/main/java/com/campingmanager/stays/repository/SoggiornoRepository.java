package com.campingmanager.stays.repository;

import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SoggiornoRepository extends JpaRepository<Soggiorno, Long> {

    List<Soggiorno> findByStatus(SoggiornoStatus status);

    List<Soggiorno> findByAccommodationId(Long accommodationId);

    List<Soggiorno> findByCheckInDate(LocalDate date);

    List<Soggiorno> findByCheckOutDate(LocalDate date);

    /**
     * True se esiste gia un soggiorno (non cancellato) che si sovrappone alle date indicate
     * per lo stesso alloggio. Due intervalli si sovrappongono se inizio1 &lt; fine2 e fine1 &gt; inizio2.
     */
    @Query("""
            SELECT COUNT(s) > 0 FROM Soggiorno s
            WHERE s.accommodation.id = :accommodationId
              AND s.status <> :excludedStatus
              AND s.checkInDate < :checkOut
              AND s.checkOutDate > :checkIn
            """)
    boolean existsOverlap(@Param("accommodationId") Long accommodationId,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut,
                          @Param("excludedStatus") SoggiornoStatus excludedStatus);

    /**
     * Numero di alloggi distinti occupati (soggiorni non cancellati) in un intervallo.
     */
    @Query("""
            SELECT COUNT(DISTINCT s.accommodation.id)
            FROM Soggiorno s
            WHERE s.status <> com.campingmanager.stays.entity.SoggiornoStatus.CANCELLATO
              AND s.checkInDate < :to
              AND s.checkOutDate > :from
            """)
    long countOccupied(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
