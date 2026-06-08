package com.campingmanager.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tavolo del ristorante del campeggio.
 */
@Entity
@Table(name = "restaurant_table")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private int number;

    private int seats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableLocation location = TableLocation.INTERNO;
}
