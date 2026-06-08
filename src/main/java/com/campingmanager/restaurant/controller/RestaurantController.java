package com.campingmanager.restaurant.controller;

import com.campingmanager.restaurant.dto.CreateBookingRequest;
import com.campingmanager.restaurant.dto.CreateTableRequest;
import com.campingmanager.restaurant.dto.TableBookingDTO;
import com.campingmanager.restaurant.dto.TableDTO;
import com.campingmanager.restaurant.service.RestaurantService;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;

    @GetMapping("/tables")
    public ResponseEntity<List<TableDTO>> getTables() {
        return ResponseEntity.ok(service.getAllTables());
    }

    @PostMapping("/tables")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<TableDTO> createTable(@Valid @RequestBody CreateTableRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTable(req));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<TableBookingDTO>> getBookings(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getBookings(currentUser));
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<TableBookingDTO> book(@AuthenticationPrincipal Ospite ospite,
                                                @Valid @RequestBody CreateBookingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.bookTable(ospite, req));
    }

    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<TableBookingDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelBooking(id));
    }
}
