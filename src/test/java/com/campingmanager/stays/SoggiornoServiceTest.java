package com.campingmanager.stays;

import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import com.campingmanager.stays.service.SoggiornoService;
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
class SoggiornoServiceTest {

    @Mock
    private SoggiornoRepository soggiornoRepository;
    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private SoggiornoService service;

    private CreateSoggiornoRequest request() {
        CreateSoggiornoRequest req = new CreateSoggiornoRequest();
        req.setAccommodationId(1L);
        req.setGuestName("Mario Rossi");
        req.setGuestEmail("mario@example.com");
        req.setNumGuests(2);
        req.setCheckInDate(LocalDate.of(2026, 7, 1));
        req.setCheckOutDate(LocalDate.of(2026, 7, 5)); // 4 notti
        return req;
    }

    private Chalet chalet() {
        Chalet c = new Chalet();
        c.setId(1L);
        c.setName("Chalet 1");
        c.setMaxCapacity(4);
        c.setPricePerNight(new BigDecimal("80.00"));
        return c;
    }

    @Test
    void shouldThrowWhenAccommodationNotFound() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request(), null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenDatesInvalid() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(chalet()));
        CreateSoggiornoRequest req = request();
        req.setCheckOutDate(req.getCheckInDate()); // 0 notti

        assertThatThrownBy(() -> service.create(req, null))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldThrowWhenOverlap() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(chalet()));
        when(soggiornoRepository.existsOverlap(eq(1L), any(), any(), eq(SoggiornoStatus.CANCELLATO)))
                .thenReturn(true);

        assertThatThrownBy(() -> service.create(request(), null))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldCreateAndComputePrice() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(chalet()));
        lenient().when(soggiornoRepository.existsOverlap(anyLong(), any(), any(), any())).thenReturn(false);
        when(soggiornoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SoggiornoDTO dto = service.create(request(), null);

        assertThat(dto.getStatus()).isEqualTo("PRENOTATO");
        assertThat(dto.getTotalPrice()).isEqualByComparingTo("320.00"); // 4 notti * 80
    }
}
