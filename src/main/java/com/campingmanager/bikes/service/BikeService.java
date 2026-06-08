package com.campingmanager.bikes.service;

import com.campingmanager.bikes.dto.BikeDTO;
import com.campingmanager.bikes.dto.BikeRentalDTO;
import com.campingmanager.bikes.dto.CreateBikeRequest;
import com.campingmanager.bikes.dto.CreateRentalRequest;
import com.campingmanager.bikes.entity.Bike;
import com.campingmanager.bikes.entity.BikeRental;
import com.campingmanager.bikes.entity.BikeRentalStatus;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.entity.BikeType;
import com.campingmanager.bikes.repository.BikeRentalRepository;
import com.campingmanager.bikes.repository.BikeRepository;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository bikeRepository;
    private final BikeRentalRepository rentalRepository;

    // === Bici ===

    public BikeDTO createBike(CreateBikeRequest req) {
        if (bikeRepository.existsByCode(req.getCode())) {
            throw new ConflictException("Esiste gia una bici con codice: " + req.getCode());
        }
        Bike bike = new Bike();
        bike.setCode(req.getCode());
        bike.setModel(req.getModel());
        bike.setType(BikeType.valueOf(req.getType()));
        bike.setPricePerDay(req.getPricePerDay());
        return BikeDTO.from(bikeRepository.save(bike));
    }

    public List<BikeDTO> getAll(String type, BikeStatus status) {
        return bikeRepository.findAll().stream()
                .filter(b -> type == null || b.getType().name().equalsIgnoreCase(type))
                .filter(b -> status == null || b.getStatus() == status)
                .map(BikeDTO::from)
                .toList();
    }

    public List<BikeDTO> getAvailable(LocalDate start, LocalDate end) {
        return bikeRepository.findAvailable(start, end).stream().map(BikeDTO::from).toList();
    }

    // === Noleggi ===

    public BikeRentalDTO createRental(Ospite ospite, CreateRentalRequest req) {
        Bike bike = bikeRepository.findById(req.getBikeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bici non trovata con id: " + req.getBikeId()));

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BadRequestException("La data di fine non puo precedere quella di inizio");
        }

        if (rentalRepository.existsOverlap(bike.getId(), req.getStartDate(), req.getEndDate(),
                BikeRentalStatus.CANCELLATO)) {
            throw new ConflictException("La bici e gia noleggiata nel periodo richiesto");
        }

        long days = ChronoUnit.DAYS.between(req.getStartDate(), req.getEndDate()) + 1;

        BikeRental rental = new BikeRental();
        rental.setOspite(ospite);
        rental.setBike(bike);
        rental.setStartDate(req.getStartDate());
        rental.setEndDate(req.getEndDate());
        rental.setTotalPrice(bike.getPricePerDay().multiply(BigDecimal.valueOf(days)));
        rental.setStatus(BikeRentalStatus.PENDING_PAYMENT);

        return BikeRentalDTO.from(rentalRepository.save(rental));
    }

    public List<BikeRentalDTO> getRentals(User currentUser) {
        List<BikeRental> rentals = (currentUser instanceof Ospite ospite)
                ? rentalRepository.findByOspiteId(ospite.getId())
                : rentalRepository.findAll();
        return rentals.stream().map(BikeRentalDTO::from).toList();
    }

    public BikeRentalDTO returnRental(Long id) {
        BikeRental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Noleggio non trovato con id: " + id));
        rental.setStatus(BikeRentalStatus.COMPLETATO);

        Bike bike = rental.getBike();
        bike.setStatus(BikeStatus.DISPONIBILE);
        bikeRepository.save(bike);

        return BikeRentalDTO.from(rentalRepository.save(rental));
    }
}
