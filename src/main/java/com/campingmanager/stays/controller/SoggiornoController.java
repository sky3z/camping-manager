package com.campingmanager.stays.controller;

import com.campingmanager.stays.dto.CheckInRequest;
import com.campingmanager.stays.dto.CheckInResponse;
import com.campingmanager.stays.dto.CreateSoggiornoRequest;
import com.campingmanager.stays.dto.SoggiornoDTO;
import com.campingmanager.stays.entity.SoggiornoStatus;
import com.campingmanager.stays.service.SoggiornoService;
import com.campingmanager.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Gestione dei soggiorni (prenotazioni) riservata a staff e amministratori.
 */
@RestController
@RequestMapping("/api/stays")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class SoggiornoController {

    private final SoggiornoService service;

    @GetMapping
    public ResponseEntity<List<SoggiornoDTO>> getAll(
            @RequestParam(required = false) SoggiornoStatus status,
            @RequestParam(required = false) Long accommodationId) {
        return ResponseEntity.ok(service.getAll(status, accommodationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoggiornoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<SoggiornoDTO> create(@Valid @RequestBody CreateSoggiornoRequest req,
                                               @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoggiornoDTO> update(@PathVariable Long id,
                                               @Valid @RequestBody CreateSoggiornoRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SoggiornoDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @PatchMapping("/{id}/checkin")
    public ResponseEntity<CheckInResponse> checkIn(@PathVariable Long id,
                                                   @Valid @RequestBody CheckInRequest req) {
        return ResponseEntity.ok(service.checkIn(id, req));
    }

    @PatchMapping("/{id}/checkout")
    public ResponseEntity<SoggiornoDTO> checkOut(@PathVariable Long id) {
        return ResponseEntity.ok(service.checkOut(id));
    }
}
