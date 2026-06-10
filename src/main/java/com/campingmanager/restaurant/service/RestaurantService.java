package com.campingmanager.restaurant.service;

import com.campingmanager.email.EmailService;
import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.restaurant.dto.CreateBookingRequest;
import com.campingmanager.restaurant.dto.CreateTableRequest;
import com.campingmanager.restaurant.dto.TableBookingDTO;
import com.campingmanager.restaurant.dto.TableDTO;
import com.campingmanager.restaurant.entity.RestaurantTable;
import com.campingmanager.restaurant.entity.TableBooking;
import com.campingmanager.restaurant.entity.TableBookingStatus;
import com.campingmanager.restaurant.entity.TableLocation;
import com.campingmanager.restaurant.repository.RestaurantTableRepository;
import com.campingmanager.restaurant.repository.TableBookingRepository;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantTableRepository tableRepository;
    private final TableBookingRepository bookingRepository;
    private final EmailService emailService;

    // === Tavoli ===

    public TableDTO createTable(CreateTableRequest req) {
        if (tableRepository.existsByNumber(req.getNumber())) {
            throw new ConflictException("Esiste gia un tavolo con numero: " + req.getNumber());
        }
        RestaurantTable table = new RestaurantTable();
        table.setNumber(req.getNumber());
        table.setSeats(req.getSeats());
        if (req.getLocation() != null) {
            table.setLocation(TableLocation.valueOf(req.getLocation()));
        }
        return TableDTO.from(tableRepository.save(table));
    }

    public List<TableDTO> getAllTables() {
        return tableRepository.findAll().stream().map(TableDTO::from).toList();
    }

    // === Prenotazioni ===

    public TableBookingDTO bookTable(Ospite ospite, CreateBookingRequest req) {
        RestaurantTable table = tableRepository.findById(req.getTableId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tavolo non trovato con id: " + req.getTableId()));

        if (req.getNumPeople() > table.getSeats()) {
            throw new BadRequestException("Il tavolo ha solo " + table.getSeats() + " posti");
        }

        if (bookingRepository.existsByTableIdAndBookingDateAndStatus(
                table.getId(), req.getBookingDate(), TableBookingStatus.CONFERMATA)) {
            throw new ConflictException("Il tavolo e gia prenotato per quella sera");
        }

        TableBooking booking = new TableBooking();
        booking.setOspite(ospite);
        booking.setTable(table);
        booking.setBookingDate(req.getBookingDate());
        booking.setBookingTime(req.getBookingTime());
        booking.setNumPeople(req.getNumPeople());
        booking.setStatus(TableBookingStatus.CONFERMATA);

        TableBooking saved = bookingRepository.save(booking);
        emailService.sendTableBookingConfirmation(
                ospite.getEmail(), saved.getBookingDate(), saved.getBookingTime());
        return TableBookingDTO.from(saved);
    }

    public List<TableBookingDTO> getBookings(User currentUser) {
        List<TableBooking> bookings = (currentUser instanceof Ospite ospite)
                ? bookingRepository.findByOspiteId(ospite.getId())
                : bookingRepository.findAll();
        return bookings.stream().map(TableBookingDTO::from).toList();
    }

    public TableBookingDTO cancelBooking(Long id) {
        TableBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata con id: " + id));
        booking.setStatus(TableBookingStatus.CANCELLATA);
        return TableBookingDTO.from(bookingRepository.save(booking));
    }
}
