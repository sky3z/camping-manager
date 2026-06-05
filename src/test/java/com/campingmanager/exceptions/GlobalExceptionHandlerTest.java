package com.campingmanager.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FakeController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class FakeController {
        @GetMapping("/test-404")
        void notFound() {
            throw new ResourceNotFoundException("Risorsa non trovata");
        }

        @GetMapping("/test-409")
        void conflict() {
            throw new ConflictException("Conflitto rilevato");
        }

        @GetMapping("/test-400")
        void badRequest() {
            throw new BadRequestException("Richiesta non valida");
        }
    }

    @Test
    void shouldReturn404WithStructuredBody() throws Exception {
        mockMvc.perform(get("/test-404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Risorsa non trovata"))
                .andExpect(jsonPath("$.path").value("/test-404"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn409OnConflict() throws Exception {
        mockMvc.perform(get("/test-409"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void shouldReturn400OnBadRequest() throws Exception {
        mockMvc.perform(get("/test-400"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
