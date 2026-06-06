package com.campingmanager.accommodations.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Alloggio del campeggio. Seconda gerarchia con strategia JOINED:
 * i sottotipi Chalet e Piazzola hanno la propria tabella collegata da questa.
 */
@Entity
@Table(name = "accommodation")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Min(1)
    private int maxCapacity;

    @NotNull
    @DecimalMin("0.0")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal pricePerNight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationStatus status = AccommodationStatus.DISPONIBILE;

    /**
     * Etichetta del tipo concreto (CHALET / PIAZZOLA), definita dai sottotipi.
     */
    public abstract String getType();
}
