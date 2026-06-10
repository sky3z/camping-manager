package com.campingmanager.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Invio di email transazionali tramite l'API Brevo (compatibile con SendGrid via configurazione).
 * Se la chiave non e configurata, l'invio degrada silenziosamente (logga e non lancia eccezioni),
 * cosi il resto del flusso applicativo non viene mai bloccato dall'email.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public EmailService(@Value("${email.api.key:}") String apiKey,
                        @Value("${email.from.email:noreply@campingmanager.it}") String senderEmail,
                        @Value("${email.from.name:Camping Manager}") String senderName,
                        RestClient.Builder restClientBuilder) {
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.restClient = restClientBuilder.baseUrl("https://api.brevo.com").build();
    }

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    public void send(String toEmail, String subject, String htmlContent) {
        if (!isEnabled()) {
            log.info("Email non inviata (servizio non configurato): destinatario={}, oggetto={}", toEmail, subject);
            return;
        }
        if (toEmail == null || toEmail.isBlank()) {
            return;
        }
        try {
            restClient.post()
                    .uri("/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "sender", Map.of("email", senderEmail, "name", senderName),
                            "to", List.of(Map.of("email", toEmail)),
                            "subject", subject,
                            "htmlContent", htmlContent))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Invio email fallito a {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendGuestCredentials(String toEmail, String name, String temporaryPassword) {
        String body = "<p>Benvenuto/a " + name + ",</p>"
                + "<p>il tuo account per i servizi del campeggio e attivo per la durata del soggiorno.</p>"
                + "<p>Email: " + toEmail + "<br>Password temporanea: <b>" + temporaryPassword + "</b></p>"
                + "<p>Ti consigliamo di cambiare la password al primo accesso.</p>";
        send(toEmail, "Le tue credenziali di accesso", body);
    }

    public void sendTableBookingConfirmation(String toEmail, LocalDate date, LocalTime time) {
        String body = "<p>La tua prenotazione al ristorante e confermata per il "
                + date + " alle " + time + ".</p>";
        send(toEmail, "Prenotazione ristorante confermata", body);
    }

    public void sendRentalConfirmation(String toEmail, String bikeCode) {
        String body = "<p>Il pagamento e andato a buon fine: il noleggio della bici "
                + bikeCode + " e ora attivo. Buona pedalata!</p>";
        send(toEmail, "Noleggio bici confermato", body);
    }
}
