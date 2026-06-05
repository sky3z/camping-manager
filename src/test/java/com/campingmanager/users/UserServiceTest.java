package com.campingmanager.users;

import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.users.dto.CreateStaffRequest;
import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.Staff;
import com.campingmanager.users.repository.UserRepository;
import com.campingmanager.users.service.FileStorageService;
import com.campingmanager.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private CreateStaffRequest staffRequest() {
        CreateStaffRequest req = new CreateStaffRequest();
        req.setEmail("staff@camping.it");
        req.setPassword("Password123!");
        req.setName("Luca");
        req.setSurname("Verdi");
        req.setDepartment("RECEPTION");
        return req;
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowWhenCreatingStaffWithExistingEmail() {
        when(userRepository.existsByEmail("staff@camping.it")).thenReturn(true);

        assertThatThrownBy(() -> userService.createStaff(staffRequest()))
                .isInstanceOf(ConflictException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldCreateStaffWithEncodedPassword() {
        when(userRepository.existsByEmail("staff@camping.it")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO dto = userService.createStaff(staffRequest());

        assertThat(dto.getRole()).isEqualTo("STAFF");
        assertThat(dto.getEmail()).isEqualTo("staff@camping.it");
        verify(userRepository).save(any(Staff.class));
    }

    @Test
    void shouldThrowWhenDeletingMissingUser() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
