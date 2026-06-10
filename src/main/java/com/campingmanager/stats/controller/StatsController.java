package com.campingmanager.stats.controller;

import com.campingmanager.stats.dto.BikeTypeCountDTO;
import com.campingmanager.stats.dto.BusyNightDTO;
import com.campingmanager.stats.dto.MonthlyRevenueDTO;
import com.campingmanager.stats.dto.OccupancyDTO;
import com.campingmanager.stats.service.StatsService;
import com.campingmanager.stays.dto.SoggiornoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Statistiche avanzate riservate all'amministratore.
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatsController {

    private final StatsService service;

    @GetMapping("/occupancy")
    public ResponseEntity<OccupancyDTO> occupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.getOccupancy(from, to));
    }

    @GetMapping("/rental-revenue")
    public ResponseEntity<List<MonthlyRevenueDTO>> rentalRevenue() {
        return ResponseEntity.ok(service.getRentalRevenue());
    }

    @GetMapping("/popular-bikes")
    public ResponseEntity<List<BikeTypeCountDTO>> popularBikes() {
        return ResponseEntity.ok(service.getPopularBikes());
    }

    @GetMapping("/busy-nights")
    public ResponseEntity<List<BusyNightDTO>> busyNights() {
        return ResponseEntity.ok(service.getBusyNights());
    }

    @GetMapping("/in-house")
    public ResponseEntity<List<SoggiornoDTO>> inHouse() {
        return ResponseEntity.ok(service.getInHouse());
    }
}
