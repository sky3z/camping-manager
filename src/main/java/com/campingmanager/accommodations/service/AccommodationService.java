package com.campingmanager.accommodations.service;

import com.campingmanager.accommodations.dto.AccommodationDTO;
import com.campingmanager.accommodations.dto.CreateAccommodationRequest;
import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.entity.Piazzola;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository repository;

    public List<AccommodationDTO> getAll(String type, AccommodationStatus status) {
        return repository.findAll().stream()
                .filter(a -> type == null || a.getType().equalsIgnoreCase(type))
                .filter(a -> status == null || a.getStatus() == status)
                .map(AccommodationDTO::from)
                .toList();
    }

    public AccommodationDTO getById(Long id) {
        return AccommodationDTO.from(findEntity(id));
    }

    public AccommodationDTO create(CreateAccommodationRequest req) {
        Accommodation accommodation = switch (req.getType()) {
            case "CHALET" -> buildChalet(new Chalet(), req);
            case "PIAZZOLA" -> buildPiazzola(new Piazzola(), req);
            default -> throw new IllegalArgumentException("Tipo non valido: " + req.getType());
        };
        applyCommonFields(accommodation, req);
        return AccommodationDTO.from(repository.save(accommodation));
    }

    public AccommodationDTO update(Long id, CreateAccommodationRequest req) {
        Accommodation accommodation = findEntity(id);
        applyCommonFields(accommodation, req);
        if (accommodation instanceof Chalet c) {
            buildChalet(c, req);
        } else if (accommodation instanceof Piazzola p) {
            buildPiazzola(p, req);
        }
        return AccommodationDTO.from(repository.save(accommodation));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Alloggio non trovato con id: " + id);
        }
        repository.deleteById(id);
    }

    private Accommodation findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alloggio non trovato con id: " + id));
    }

    private void applyCommonFields(Accommodation a, CreateAccommodationRequest req) {
        a.setName(req.getName());
        a.setDescription(req.getDescription());
        a.setMaxCapacity(req.getMaxCapacity());
        a.setPricePerNight(req.getPricePerNight());
    }

    private Chalet buildChalet(Chalet c, CreateAccommodationRequest req) {
        if (req.getRooms() != null) c.setRooms(req.getRooms());
        if (req.getBeds() != null) c.setBeds(req.getBeds());
        if (req.getHasBathroom() != null) c.setHasBathroom(req.getHasBathroom());
        if (req.getHasKitchen() != null) c.setHasKitchen(req.getHasKitchen());
        if (req.getHasAc() != null) c.setHasAc(req.getHasAc());
        return c;
    }

    private Piazzola buildPiazzola(Piazzola p, CreateAccommodationRequest req) {
        if (req.getTipoPiazzola() != null) {
            p.setTipoPiazzola(Piazzola.TipoPiazzola.valueOf(req.getTipoPiazzola()));
        }
        if (req.getSurfaceM2() != null) p.setSurfaceM2(req.getSurfaceM2());
        if (req.getHasElectricity() != null) p.setHasElectricity(req.getHasElectricity());
        if (req.getHasWater() != null) p.setHasWater(req.getHasWater());
        return p;
    }
}
