package com.campingmanager.stays.service;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.email.EmailService;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.stays.dto.CheckInRequest;
import com.campingmanager.stays.dto.CheckInResponse;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.dto.TodayOverviewDTO;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.Staff;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SoggiornoService {

    private final SoggiornoRepository soggiornoRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public SoggiornoDTO create(CreateSoggiornoRequest req, User currentUser) {
        Accommodation accommodation = accommodationRepository.findById(req.getAccommodationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alloggio non trovato con id: " + req.getAccommodationId()));

        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        if (nights <= 0) {
            throw new BadRequestException("La data di check-out deve essere successiva al check-in");
        }

        if (soggiornoRepository.existsOverlap(accommodation.getId(),
                req.getCheckInDate(), req.getCheckOutDate(), SoggiornoStatus.CANCELLATO)) {
            throw new ConflictException("L'alloggio e gia prenotato nelle date richieste");
        }

        Soggiorno soggiorno = new Soggiorno();
        soggiorno.setAccommodation(accommodation);
        soggiorno.setGuestName(req.getGuestName());
        soggiorno.setGuestEmail(req.getGuestEmail());
        soggiorno.setNumGuests(req.getNumGuests());
        soggiorno.setCheckInDate(req.getCheckInDate());
        soggiorno.setCheckOutDate(req.getCheckOutDate());
        soggiorno.setStatus(SoggiornoStatus.PRENOTATO);
        soggiorno.setTotalPrice(accommodation.getPricePerNight().multiply(BigDecimal.valueOf(nights)));
        if (currentUser instanceof Staff staff) {
            soggiorno.setCreatedByStaff(staff);
        }

        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    public List<SoggiornoDTO> getAll(SoggiornoStatus status, Long accommodationId) {
        List<Soggiorno> list;
        if (status != null) {
            list = soggiornoRepository.findByStatus(status);
        } else if (accommodationId != null) {
            list = soggiornoRepository.findByAccommodationId(accommodationId);
        } else {
            list = soggiornoRepository.findAll();
        }
        return list.stream()
                .filter(s -> accommodationId == null || s.getAccommodation().getId().equals(accommodationId))
                .map(SoggiornoDTO::from)
                .toList();
    }

    public SoggiornoDTO getById(Long id) {
        return SoggiornoDTO.from(findEntity(id));
    }

    public SoggiornoDTO update(Long id, CreateSoggiornoRequest req) {
        Soggiorno soggiorno = findEntity(id);
        if (soggiorno.getStatus() == SoggiornoStatus.CHECKED_OUT
                || soggiorno.getStatus() == SoggiornoStatus.CANCELLATO) {
            throw new BadRequestException("Non e possibile modificare un soggiorno concluso o cancellato");
        }

        long nights = ChronoUnit.DAYS.between(req.getCheckInDate(), req.getCheckOutDate());
        if (nights <= 0) {
            throw new BadRequestException("La data di check-out deve essere successiva al check-in");
        }

        soggiorno.setGuestName(req.getGuestName());
        soggiorno.setGuestEmail(req.getGuestEmail());
        soggiorno.setNumGuests(req.getNumGuests());
        soggiorno.setCheckInDate(req.getCheckInDate());
        soggiorno.setCheckOutDate(req.getCheckOutDate());
        soggiorno.setTotalPrice(
                soggiorno.getAccommodation().getPricePerNight().multiply(BigDecimal.valueOf(nights)));

        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    public SoggiornoDTO cancel(Long id) {
        Soggiorno soggiorno = findEntity(id);
        soggiorno.setStatus(SoggiornoStatus.CANCELLATO);
        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    /**
     * Check-in: crea l'account ospite (valido fino al check-out), segna il soggiorno
     * come CHECKED_IN e l'alloggio come OCCUPATA. Restituisce la password temporanea.
     */
    public CheckInResponse checkIn(Long id, CheckInRequest req) {
        Soggiorno soggiorno = findEntity(id);
        if (soggiorno.getStatus() != SoggiornoStatus.PRENOTATO) {
            throw new BadRequestException("Il check-in e possibile solo per soggiorni in stato PRENOTATO");
        }
        if (userRepository.existsByEmail(soggiorno.getGuestEmail())) {
            throw new ConflictException("Esiste gia un account con email: " + soggiorno.getGuestEmail());
        }

        String temporaryPassword = generatePassword();

        Ospite ospite = new Ospite();
        ospite.setEmail(soggiorno.getGuestEmail());
        ospite.setPassword(passwordEncoder.encode(temporaryPassword));
        String[] parts = soggiorno.getGuestName().trim().split("\\s+", 2);
        ospite.setName(parts[0]);
        ospite.setSurname(parts.length > 1 ? parts[1] : "-");
        ospite.setDocumentNumber(req.getDocumentNumber());
        ospite.setNationality(req.getNationality());
        ospite.setPhone(req.getPhone());
        ospite.setBirthDate(req.getBirthDate());
        ospite.setAccountValidUntil(soggiorno.getCheckOutDate());
        Ospite savedOspite = (Ospite) userRepository.save(ospite);

        soggiorno.setOspiteAccount(savedOspite);
        soggiorno.setStatus(SoggiornoStatus.CHECKED_IN);

        Accommodation accommodation = soggiorno.getAccommodation();
        accommodation.setStatus(AccommodationStatus.OCCUPATA);
        accommodationRepository.save(accommodation);

        Soggiorno saved = soggiornoRepository.save(soggiorno);

        emailService.sendGuestCredentials(savedOspite.getEmail(), savedOspite.getName(), temporaryPassword);

        return new CheckInResponse(SoggiornoDTO.from(saved), savedOspite.getEmail(), temporaryPassword);
    }

    /**
     * Check-out: chiude il soggiorno e libera l'alloggio.
     */
    public SoggiornoDTO checkOut(Long id) {
        Soggiorno soggiorno = findEntity(id);
        if (soggiorno.getStatus() != SoggiornoStatus.CHECKED_IN) {
            throw new BadRequestException("Il check-out e possibile solo per soggiorni in stato CHECKED_IN");
        }
        soggiorno.setStatus(SoggiornoStatus.CHECKED_OUT);

        Accommodation accommodation = soggiorno.getAccommodation();
        accommodation.setStatus(AccommodationStatus.DISPONIBILE);
        accommodationRepository.save(accommodation);

        return SoggiornoDTO.from(soggiornoRepository.save(soggiorno));
    }

    /**
     * Arrivi (check-in previsti) e partenze (check-out previsti) di una data (default: oggi).
     */
    public TodayOverviewDTO getToday(LocalDate date) {
        LocalDate day = (date == null) ? LocalDate.now() : date;
        List<SoggiornoDTO> arrivals = soggiornoRepository.findByCheckInDate(day).stream()
                .map(SoggiornoDTO::from).toList();
        List<SoggiornoDTO> departures = soggiornoRepository.findByCheckOutDate(day).stream()
                .map(SoggiornoDTO::from).toList();
        return new TodayOverviewDTO(day, arrivals, departures);
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    private Soggiorno findEntity(Long id) {
        return soggiornoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soggiorno non trovato con id: " + id));
    }
}
