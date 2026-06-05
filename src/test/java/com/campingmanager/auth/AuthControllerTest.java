package com.campingmanager.auth;

import com.campingmanager.auth.dto.LoginRequest;
import com.campingmanager.users.entity.Admin;
import com.campingmanager.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void seed() {
        userRepository.deleteAll();
        Admin admin = new Admin();
        admin.setEmail("admin@camping.it");
        admin.setPassword(passwordEncoder.encode("Password123!"));
        admin.setName("Anna");
        admin.setSurname("Bianchi");
        userRepository.save(admin);
    }

    @Test
    void shouldLoginAndReturnToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@camping.it");
        req.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@camping.it"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void shouldReturn401OnWrongPassword() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@camping.it");
        req.setPassword("sbagliata");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400OnMissingFields() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
