package com.campingmanager.users.service;

import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.exceptions.ConflictException;
import com.campingmanager.exceptions.ResourceNotFoundException;
import com.campingmanager.users.dto.ChangePasswordRequest;
import com.campingmanager.users.dto.CreateStaffRequest;
import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.Admin;
import com.campingmanager.users.entity.Ospite;
import com.campingmanager.users.entity.Role;
import com.campingmanager.users.entity.Staff;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO getMe(User currentUser) {
        return UserResponseDTO.from(currentUser);
    }

    public UserResponseDTO updateAvatar(User currentUser, MultipartFile file) {
        String path = fileStorageService.store(file);
        currentUser.setProfileImage(path);
        userRepository.save(currentUser);
        return UserResponseDTO.from(currentUser);
    }

    public void changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new BadRequestException("La password attuale non e corretta");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }

    // === Gestione utenti (ADMIN) ===

    public Page<UserResponseDTO> getAllUsers(Pageable pageable, Role roleFilter) {
        Page<User> users = (roleFilter == null)
                ? userRepository.findAll(pageable)
                : userRepository.findByType(roleToClass(roleFilter), pageable);
        return users.map(UserResponseDTO::from);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con id: " + id));
        return UserResponseDTO.from(user);
    }

    public UserResponseDTO createStaff(CreateStaffRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email gia registrata: " + request.getEmail());
        }
        Staff staff = new Staff();
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setName(request.getName());
        staff.setSurname(request.getSurname());
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            staff.setDepartment(Staff.Department.valueOf(request.getDepartment()));
        }
        staff.setHireDate(LocalDate.now());
        return UserResponseDTO.from(userRepository.save(staff));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utente non trovato con id: " + id);
        }
        userRepository.deleteById(id);
    }

    private Class<? extends User> roleToClass(Role role) {
        return switch (role) {
            case ADMIN -> Admin.class;
            case STAFF -> Staff.class;
            case OSPITE -> Ospite.class;
        };
    }
}
