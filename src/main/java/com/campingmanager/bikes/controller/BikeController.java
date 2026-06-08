package com.campingmanager.bikes.controller;

import com.campingmanager.bikes.dto.BikeDTO;
import com.campingmanager.bikes.dto.BikeRentalDTO;
import com.campingmanager.bikes.dto.CreateBikeRequest;
import com.campingmanager.bikes.dto.CreateRentalRequest;
import com.campingmanager.bikes.entity.BikeStatus;
import com.campingmanager.bikes.service.BikeService;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@RequiredArgsConstructor
public class BikeController {

    private final BikeService service;

    @GetMapping
    public ResponseEntity<List<BikeDTO>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BikeStatus status) {
        return ResponseEntity.ok(service.getAll(type, status));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<BikeDTO> createBike(@Valid @RequestBody CreateBikeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBike(req));
    }

    @GetMapping("/available")
    public ResponseEntity<List<BikeDTO>> getAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.getAvailable(start, end));
    }

    @PostMapping("/rentals")
    @PreAuthorize("hasRole('OSPITE')")
    public ResponseEntity<BikeRentalDTO> createRental(@AuthenticationPrincipal Ospite ospite,
                                                      @Valid @RequestBody CreateRentalRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createRental(ospite, req));
    }

    @GetMapping("/rentals")
    public ResponseEntity<List<BikeRentalDTO>> getRentals(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(service.getRentals(currentUser));
    }

    @PatchMapping("/rentals/{id}/return")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<BikeRentalDTO> returnRental(@PathVariable Long id) {
        return ResponseEntity.ok(service.returnRental(id));
    }
}
