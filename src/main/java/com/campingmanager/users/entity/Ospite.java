package com.campingmanager.users.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Ospite del campeggio. L'account viene creato dallo staff al check-in
 * ed e valido per la durata del soggiorno ({@code accountValidUntil}).
 * Il legame con il Soggiorno e la disattivazione automatica dopo il check-out
 * verranno aggiunti in Fase 2.
 */
@Entity
@Table(name = "ospite")
@DiscriminatorValue("OSPITE")
@Getter
@Setter
@NoArgsConstructor
public class Ospite extends User {

    private String documentNumber;
    private String nationality;
    private String phone;
    private LocalDate birthDate;
    private LocalDate accountValidUntil;

    @Override
    public Role getRole() {
        return Role.OSPITE;
    }
}
