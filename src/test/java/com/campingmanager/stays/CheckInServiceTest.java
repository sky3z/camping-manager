package com.campingmanager.stays;

import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.stays.dto.CheckInRequest;
import com.campingmanager.stays.dto.CheckInResponse;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import com.campingmanager.stays.service.SoggiornoService;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock
    private SoggiornoRepository soggiornoRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SoggiornoService service;

    private Soggiorno prenotato() {
        Chalet c = new Chalet();
        c.setId(1L);
        c.setName("Chalet 1");
        c.setPricePerNight(new BigDecimal("80.00"));
        c.setStatus(AccommodationStatus.DISPONIBILE);

        Soggiorno s = new Soggiorno();
        s.setId(10L);
        s.setAccommodation(c);
        s.setGuestName("Mario Rossi");
        s.setGuestEmail("mario@example.com");
        s.setNumGuests(2);
        s.setCheckInDate(LocalDate.now());
        s.setCheckOutDate(LocalDate.now().plusDays(3));
        s.setStatus(SoggiornoStatus.PRENOTATO);
        return s;
    }

    @Test
    void shouldCheckInAndCreateOspite() {
        Soggiorno s = prenotato();
        when(soggiornoRepository.findById(10L)).thenReturn(Optional.of(s));
        when(userRepository.existsByEmail("mario@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(soggiornoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CheckInRequest req = new CheckInRequest();
        req.setDocumentNumber("AB123");

        CheckInResponse resp = service.checkIn(10L, req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(Ospite.class);
        Ospite ospite = (Ospite) captor.getValue();
        assertThat(ospite.getEmail()).isEqualTo("mario@example.com");
        assertThat(ospite.getName()).isEqualTo("Mario");
        assertThat(ospite.getSurname()).isEqualTo("Rossi");
        assertThat(ospite.getAccountValidUntil()).isEqualTo(s.getCheckOutDate());

        assertThat(s.getStatus()).isEqualTo(SoggiornoStatus.CHECKED_IN);
        assertThat(s.getAccommodation().getStatus()).isEqualTo(AccommodationStatus.OCCUPATA);
        assertThat(resp.getTemporaryPassword()).isNotBlank();
        assertThat(resp.getOspiteEmail()).isEqualTo("mario@example.com");
    }

    @Test
    void shouldRejectCheckInWhenNotPrenotato() {
        Soggiorno s = prenotato();
        s.setStatus(SoggiornoStatus.CHECKED_IN);
        when(soggiornoRepository.findById(10L)).thenReturn(Optional.of(s));

        assertThatThrownBy(() -> service.checkIn(10L, new CheckInRequest()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldRejectCheckInWhenEmailExists() {
        Soggiorno s = prenotato();
        when(soggiornoRepository.findById(10L)).thenReturn(Optional.of(s));
        when(userRepository.existsByEmail("mario@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.checkIn(10L, new CheckInRequest()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldCheckOutAndFreeAccommodation() {
        Soggiorno s = prenotato();
        s.setStatus(SoggiornoStatus.CHECKED_IN);
        s.getAccommodation().setStatus(AccommodationStatus.OCCUPATA);
        when(soggiornoRepository.findById(10L)).thenReturn(Optional.of(s));
        when(soggiornoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SoggiornoDTO dto = service.checkOut(10L);

        assertThat(s.getStatus()).isEqualTo(SoggiornoStatus.CHECKED_OUT);
        assertThat(s.getAccommodation().getStatus()).isEqualTo(AccommodationStatus.DISPONIBILE);
        assertThat(dto.getStatus()).isEqualTo("CHECKED_OUT");
    }
}
