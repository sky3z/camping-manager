package com.campingmanager.users.service;

import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.users.dto.ChangePasswordRequest;
import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
}
