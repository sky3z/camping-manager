package com.campingmanager.stays.repository;

import com.campingmanager.stays.entity.Soggiorno;
import com.campingmanager.stays.entity.SoggiornoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoggiornoRepository extends JpaRepository<Soggiorno, Long> {

    List<Soggiorno> findByStatus(SoggiornoStatus status);
}
