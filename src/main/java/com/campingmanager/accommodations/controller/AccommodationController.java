package com.campingmanager.accommodations.controller;

import com.campingmanager.accommodations.dto.AccommodationDTO;
import com.campingmanager.accommodations.dto.CreateAccommodationRequest;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import com.campingmanager.accommodations.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService service;

    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) AccommodationStatus status) {
        return ResponseEntity.ok(service.getAll(type, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> create(@Valid @RequestBody CreateAccommodationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccommodationDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody CreateAccommodationRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
