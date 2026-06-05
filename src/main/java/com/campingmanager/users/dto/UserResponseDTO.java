package com.campingmanager.users.dto;

import com.campingmanager.users.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Vista pubblica di un utente (senza password).
 */
@Data
public class UserResponseDTO {

    private Long id;
    private String email;
    private String name;
    private String surname;
    private String profileImage;
    private String role;
    private LocalDateTime createdAt;

    public static UserResponseDTO from(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setProfileImage(user.getProfileImage());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
