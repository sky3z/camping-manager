package com.campingmanager.stats;

import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.payments.repository.PaymentRepository;
import com.campingmanager.restaurant.repository.TableBookingRepository;
import com.campingmanager.stats.dto.BikeTypeCountDTO;
import com.campingmanager.stats.dto.MonthlyRevenueDTO;
import com.campingmanager.stats.dto.OccupancyDTO;
import com.campingmanager.stats.service.StatsService;
import com.campingmanager.stays.repository.SoggiornoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private SoggiornoRepository soggiornoRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BikeRentalRepository rentalRepository;
    @Mock
    private TableBookingRepository bookingRepository;

    @InjectMocks
    private StatsService service;

    @Test
    void shouldComputeOccupancyRate() {
        when(soggiornoRepository.countOccupied(any(), any())).thenReturn(3L);
        when(accommodationRepository.count()).thenReturn(20L);

        OccupancyDTO dto = service.getOccupancy(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5));

        assertThat(dto.getOccupied()).isEqualTo(3L);
        assertThat(dto.getTotal()).isEqualTo(20L);
        assertThat(dto.getOccupancyRate()).isEqualTo(15.0);
    }

    @Test
    void shouldReturnZeroOccupancyWhenNoAccommodations() {
        when(soggiornoRepository.countOccupied(any(), any())).thenReturn(0L);
        when(accommodationRepository.count()).thenReturn(0L);

        OccupancyDTO dto = service.getOccupancy(LocalDate.now(), LocalDate.now().plusDays(1));

        assertThat(dto.getOccupancyRate()).isEqualTo(0.0);
    }

    @Test
    void shouldMapRentalRevenue() {
        when(paymentRepository.revenueByMonth()).thenReturn(List.of(
                new Object[]{2026, 7, new BigDecimal("135.00")},
                new Object[]{2026, 8, new BigDecimal("90.00")}));

        List<MonthlyRevenueDTO> revenue = service.getRentalRevenue();

        assertThat(revenue).hasSize(2);
        assertThat(revenue.get(0).getYear()).isEqualTo(2026);
        assertThat(revenue.get(0).getMonth()).isEqualTo(7);
        assertThat(revenue.get(0).getTotal()).isEqualByComparingTo("135.00");
    }

    @Test
    void shouldMapPopularBikes() {
        when(rentalRepository.countByBikeType()).thenReturn(List.of(
                new Object[]{com.campingmanager.bikes.entity.BikeType.CITY, 5L},
                new Object[]{com.campingmanager.bikes.entity.BikeType.EBIKE, 2L}));

        List<BikeTypeCountDTO> bikes = service.getPopularBikes();

        assertThat(bikes).hasSize(2);
        assertThat(bikes.get(0).getType()).isEqualTo("CITY");
        assertThat(bikes.get(0).getCount()).isEqualTo(5L);
    }
}
