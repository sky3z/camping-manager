package com.campingmanager.stays.service;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.repository.SoggiornoRepository;
import com.campingmanager.users.entity.Staff;
import com.campingmanager.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoggiornoService {

    private final SoggiornoRepository soggiornoRepository;
    private final AccommodationRepository accommodationRepository;

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

    private Soggiorno findEntity(Long id) {
        return soggiornoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Soggiorno non trovato con id: " + id));
    }
}
