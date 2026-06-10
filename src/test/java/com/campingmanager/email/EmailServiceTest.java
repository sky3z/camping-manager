package com.campingmanager.email;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EmailServiceTest {

    private EmailService service(String key) {
        return new EmailService(key, "noreply@test.it", "Camping", RestClient.builder());
    }

    @Test
    void shouldBeDisabledWhenNoApiKey() {
        assertThat(service("").isEnabled()).isFalse();
        assertThat(service("any-key").isEnabled()).isTrue();
    }

    @Test
    void shouldNotThrowWhenSendingWithoutApiKey() {
        EmailService service = service("");
        assertThatCode(() -> {
            service.send("a@b.it", "oggetto", "<p>ciao</p>");
            service.sendGuestCredentials("a@b.it", "Mario", "pw123");
            service.sendTableBookingConfirmation("a@b.it", LocalDate.now(), LocalTime.NOON);
            service.sendRentalConfirmation("a@b.it", "BIKE-01");
        }).doesNotThrowAnyException();
    }
}
