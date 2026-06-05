package com.campingmanager.users.repository;

import com.campingmanager.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Accesso ai dati di tutti gli utenti (qualsiasi sottotipo).
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Filtra gli utenti per sottotipo concreto (es. Admin.class, Staff.class, Ospite.class).
     */
    @Query("SELECT u FROM User u WHERE TYPE(u) = :type")
    Page<User> findByType(@Param("type") Class<? extends User> type, Pageable pageable);
}
