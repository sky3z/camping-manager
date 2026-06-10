package com.campingmanager.stats.service;

import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.payments.repository.PaymentRepository;
import com.campingmanager.restaurant.repository.TableBookingRepository;
import com.campingmanager.stats.dto.BikeTypeCountDTO;
import com.campingmanager.stats.dto.BusyNightDTO;
import com.campingmanager.stats.dto.MonthlyRevenueDTO;
import com.campingmanager.stats.dto.OccupancyDTO;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final SoggiornoRepository soggiornoRepository;
    private final AccommodationRepository accommodationRepository;
    private final PaymentRepository paymentRepository;
    private final BikeRentalRepository rentalRepository;
    private final TableBookingRepository bookingRepository;

    public OccupancyDTO getOccupancy(LocalDate from, LocalDate to) {
        long occupied = soggiornoRepository.countOccupied(from, to);
        long total = accommodationRepository.count();
        double rate = (total == 0) ? 0.0 : Math.round(occupied * 10000.0 / total) / 100.0;
        return new OccupancyDTO(from, to, occupied, total, rate);
    }

    public List<MonthlyRevenueDTO> getRentalRevenue() {
        return paymentRepository.revenueByMonth().stream()
                .map(r -> new MonthlyRevenueDTO(
                        ((Number) r[0]).intValue(),
                        ((Number) r[1]).intValue(),
                        (BigDecimal) r[2]))
                .toList();
    }

    public List<BikeTypeCountDTO> getPopularBikes() {
        return rentalRepository.countByBikeType().stream()
                .map(r -> new BikeTypeCountDTO(
                        String.valueOf(r[0]),
                        ((Number) r[1]).longValue()))
                .toList();
    }

    public List<BusyNightDTO> getBusyNights() {
        return bookingRepository.countByNight().stream()
                .map(r -> new BusyNightDTO(
                        (LocalDate) r[0],
                        ((Number) r[1]).longValue()))
                .toList();
    }

    public List<SoggiornoDTO> getInHouse() {
        return soggiornoRepository.findByStatus(SoggiornoStatus.CHECKED_IN).stream()
                .map(SoggiornoDTO::from)
                .toList();
    }
}
