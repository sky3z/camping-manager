package com.campingmanager.bikes.repository;

import com.campingmanager.bikes.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BikeRepository extends JpaRepository<Bike, Long> {

    boolean existsByCode(String code);

    /**
     * Bici disponibili (non in manutenzione) senza noleggi attivi/in attesa
     * che si sovrappongono al periodo richiesto.
     */
    @Query("""
            SELECT b FROM Bike b
            WHERE b.status <> com.campingmanager.bikes.entity.BikeStatus.MANUTENZIONE
              AND b.id NOT IN (
                  SELECT r.bike.id FROM BikeRental r
                  WHERE r.status <> com.campingmanager.bikes.entity.BikeRentalStatus.CANCELLATO
                    AND r.startDate <= :end
                    AND r.endDate >= :start
              )
            ORDER BY b.pricePerDay ASC
            """)
    List<Bike> findAvailable(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
