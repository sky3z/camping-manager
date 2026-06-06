package com.campingmanager.accommodations.repository;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.accommodations.entity.AccommodationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findByStatus(AccommodationStatus status);

    boolean existsByName(String name);
}
