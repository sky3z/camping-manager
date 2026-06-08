package com.campingmanager.restaurant.repository;

import com.campingmanager.restaurant.entity.TableBooking;
import com.campingmanager.restaurant.entity.TableBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TableBookingRepository extends JpaRepository<TableBooking, Long> {

    List<TableBooking> findByOspiteId(Long ospiteId);

    boolean existsByTableIdAndBookingDateAndStatus(Long tableId, LocalDate bookingDate, TableBookingStatus status);

    List<TableBooking> findByBookingDate(LocalDate bookingDate);
}
