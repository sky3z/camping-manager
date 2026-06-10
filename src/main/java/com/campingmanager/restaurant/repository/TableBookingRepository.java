package com.campingmanager.restaurant.repository;

import com.campingmanager.restaurant.entity.TableBooking;
import com.campingmanager.restaurant.entity.TableBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {

    List<TableBooking> findByOspiteId(Long ospiteId);

    boolean existsByTableIdAndBookingDateAndStatus(Long tableId, LocalDate bookingDate, TableBookingStatus status);

    List<TableBooking> findByBookingDate(LocalDate bookingDate);

    /**
     * Conteggio delle prenotazioni confermate per serata, dalla piu affollata.
     * Ritorna righe [data (LocalDate), conteggio (Long)].
     */
    @Query("""
            SELECT b.bookingDate, COUNT(b)
            FROM TableBooking b
            WHERE b.status = com.campingmanager.restaurant.entity.TableBookingStatus.CONFERMATA
            GROUP BY b.bookingDate
            ORDER BY COUNT(b) DESC
            """)
    List<Object[]> countByNight();
}
