package com.campingmanager.config;

import com.campingmanager.accommodations.entity.Chalet;
import com.campingmanager.accommodations.entity.Piazzola;
import com.campingmanager.accommodations.repository.AccommodationRepository;
import com.campingmanager.users.entity.Admin;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Inizializza i dati di base all'avvio: l'amministratore e gli alloggi della struttura
 * (10 Chalet + 10 Piazzole), creandoli solo se non sono gia presenti.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedAccommodations();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        Admin admin = new Admin();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setName("Admin");
        admin.setSurname("Sistema");
        userRepository.save(admin);
    }

    private void seedAccommodations() {
        if (accommodationRepository.count() > 0) {
            return;
        }
        for (int i = 1; i <= 10; i++) {
            Chalet chalet = new Chalet();
            chalet.setName("Chalet " + i);
            chalet.setDescription("Chalet in legno n. " + i);
            chalet.setMaxCapacity(4);
            chalet.setPricePerNight(new BigDecimal("80.00"));
            chalet.setRooms(2);
            chalet.setBeds(4);
            chalet.setHasBathroom(true);
            chalet.setHasKitchen(true);
            chalet.setHasAc(true);
            accommodationRepository.save(chalet);
        }
        for (int i = 1; i <= 10; i++) {
            Piazzola piazzola = new Piazzola();
            piazzola.setName("Piazzola " + i);
            piazzola.setDescription("Piazzola in erba n. " + i);
            piazzola.setMaxCapacity(6);
            piazzola.setPricePerNight(new BigDecimal("25.00"));
            piazzola.setTipoPiazzola(Piazzola.TipoPiazzola.CAMPER);
            piazzola.setSurfaceM2(80.0);
            piazzola.setHasElectricity(true);
            piazzola.setHasWater(true);
            accommodationRepository.save(piazzola);
        }
    }
}
