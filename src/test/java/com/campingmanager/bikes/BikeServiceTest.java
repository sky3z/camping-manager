package com.campingmanager.bikes;

import com.campingmanager.bikes.dto.BikeRentalDTO;
import com.campingmanager.bikes.dto.CreateRentalRequest;
import com.campingmanager.bikes.entity.Bike;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.bikes.repository.BikeRepository;
import com.campingmanager.bikes.service.BikeService;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.users.entity.Ospite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BikeServiceTest {

    @Mock
    private BikeRepository bikeRepository;
    @Mock
    private BikeRentalRepository rentalRepository;

    @InjectMocks
    private BikeService service;

    private Bike bike() {
        Bike b = new Bike();
        b.setId(1L);
        b.setCode("BIKE-01");
        b.setPricePerDay(new BigDecimal("15.00"));
        return b;
    }

    private CreateRentalRequest request(LocalDate start, LocalDate end) {
        CreateRentalRequest req = new CreateRentalRequest();
        req.setBikeId(1L);
        req.setStartDate(start);
        req.setEndDate(end);
        return req;
    }

    @Test
    void shouldThrowWhenBikeNotFound() {
        when(bikeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createRental(new Ospite(),
                request(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3))))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenDatesInvalid() {
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(bike()));

        assertThatThrownBy(() -> service.createRental(new Ospite(),
                request(LocalDate.of(2026, 7, 3), LocalDate.of(2026, 7, 1))))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldThrowWhenBikeAlreadyRented() {
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(bike()));
        when(rentalRepository.existsOverlap(eq(1L), any(), any(), eq(BikeRentalStatus.CANCELLATO)))
                .thenReturn(true);

        assertThatThrownBy(() -> service.createRental(new Ospite(),
                request(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3))))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldCreateRentalPendingPaymentWithPrice() {
        when(bikeRepository.findById(1L)).thenReturn(Optional.of(bike()));
        lenient().when(rentalRepository.existsOverlap(anyLong(), any(), any(), any())).thenReturn(false);
        when(rentalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BikeRentalDTO dto = service.createRental(new Ospite(),
                request(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3)));

        assertThat(dto.getStatus()).isEqualTo("PENDING_PAYMENT");
        // 3 giorni inclusivi (1,2,3) * 15 = 45
        assertThat(dto.getTotalPrice()).isEqualByComparingTo("45.00");
    }
}
