package com.campingmanager.users.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Personale della struttura. Gestisce soggiorni, check-in/out e servizi.
 */
@Entity
@Table(name = "staff")
@DiscriminatorValue("STAFF")
@Getter
@Setter
@NoArgsConstructor
public class Staff extends User {

    public enum Department {
        RECEPTION,
        RISTORANTE,
        MANUTENZIONE
    }

    @Enumerated(EnumType.STRING)
    private Department department;

    private LocalDate hireDate;

    @Override
    public Role getRole() {
        return Role.STAFF;
    }
}
