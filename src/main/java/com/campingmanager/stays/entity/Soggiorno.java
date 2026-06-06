package com.campingmanager.stays.entity;

import com.campingmanager.accommodations.entity.Accommodation;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.Staff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Soggiorno (prenotazione) gestito dallo staff: alloggio, date, ospite di riferimento.
 * L'account ospite ({@link #ospiteAccount}) viene creato al check-in.
 */
@Entity
@Table(name = "soggiorno")
@Getter
@Setter
@NoArgsConstructor
public class Soggiorno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Column(nullable = false)
    private String guestName;

    @Column(nullable = false)
    private String guestEmail;

    private int numGuests;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SoggiornoStatus status = SoggiornoStatus.PRENOTATO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_staff_id")
    private Staff createdByStaff;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ospite_account_id")
    private Ospite ospiteAccount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
