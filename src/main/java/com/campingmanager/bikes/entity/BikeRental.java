package com.campingmanager.bikes.entity;

import com.campingmanager.users.entity.Ospite;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Noleggio di una bici da parte di un ospite. Diventa ATTIVO dopo il pagamento.
 */
@Entity
@Table(name = "bike_rental")
@Getter
@Setter
@NoArgsConstructor
public class BikeRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ospite_id", nullable = false)
    private Ospite ospite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BikeRentalStatus status = BikeRentalStatus.PENDING_PAYMENT;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
