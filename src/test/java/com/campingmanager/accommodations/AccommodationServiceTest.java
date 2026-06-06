package com.campingmanager.accommodations;

import com.campingmanager.accommodations.dto.AccommodationDTO;
import com.campingmanager.accommodations.dto.CreateAccommodationRequest;
import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.accommodations.service.AccommodationService;
import com.campingmanager.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {

    @Mock
    private AccommodationRepository repository;

    @InjectMocks
    private AccommodationService service;

    @Test
    void shouldThrowWhenAccommodationNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldCreateChalet() {
        CreateAccommodationRequest req = new CreateAccommodationRequest();
        req.setName("Chalet 5");
        req.setType("CHALET");
        req.setMaxCapacity(4);
        req.setPricePerNight(new BigDecimal("80.00"));
        req.setRooms(2);
        req.setBeds(4);
        req.setHasBathroom(true);

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccommodationDTO dto = service.create(req);

        assertThat(dto.getType()).isEqualTo("CHALET");
        assertThat(dto.getName()).isEqualTo("Chalet 5");
        assertThat(dto.getPricePerNight()).isEqualByComparingTo("80.00");
    }

    @Test
    void shouldCreatePiazzola() {
        CreateAccommodationRequest req = new CreateAccommodationRequest();
        req.setName("Piazzola 3");
        req.setType("PIAZZOLA");
        req.setMaxCapacity(6);
        req.setPricePerNight(new BigDecimal("25.00"));
        req.setTipoPiazzola("CAMPER");
        req.setHasElectricity(true);

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AccommodationDTO dto = service.create(req);

        assertThat(dto.getType()).isEqualTo("PIAZZOLA");
        assertThat(dto.getName()).isEqualTo("Piazzola 3");
    }
}
