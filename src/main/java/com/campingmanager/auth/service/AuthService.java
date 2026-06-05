package com.campingmanager.auth.service;

import com.campingmanager.auth.dto.AuthResponse;
import com.campingmanager.auth.dto.LoginRequest;
import com.campingmanager.security.JwtUtil;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Verifica le credenziali e, se valide, restituisce un token JWT.
     */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
