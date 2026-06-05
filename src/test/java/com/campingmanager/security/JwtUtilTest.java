package com.campingmanager.security;

import com.campingmanager.users.entity.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                "chiave-di-test-lunga-almeno-32-caratteri-per-hmac-sha256-ok",
                3_600_000L
        );
    }

    private Admin admin(String email) {
        Admin admin = new Admin();
        admin.setEmail(email);
        return admin;
    }

    @Test
    void shouldGenerateAndValidateToken() {
        Admin user = admin("admin@camping.it");

        String token = jwtUtil.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("admin@camping.it");
        assertThat(jwtUtil.isTokenValid(token, user)).isTrue();
    }

    @Test
    void shouldRejectTamperedToken() {
        Admin user = admin("admin@camping.it");
        String token = jwtUtil.generateToken(user) + "manomesso";

        assertThat(jwtUtil.isTokenValid(token, user)).isFalse();
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        String token = jwtUtil.generateToken(admin("admin@camping.it"));

        assertThat(jwtUtil.isTokenValid(token, admin("altro@camping.it"))).isFalse();
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtUtil shortLived = new JwtUtil(
                "chiave-di-test-lunga-almeno-32-caratteri-per-hmac-sha256-ok",
                1L
        );
        Admin user = admin("admin@camping.it");
        String token = shortLived.generateToken(user);

        // attende che il token scada
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        assertThat(shortLived.isTokenValid(token, user)).isFalse();
    }
}
