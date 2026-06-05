package com.campingmanager.users.controller;

import com.campingmanager.users.dto.ChangePasswordRequest;
import com.campingmanager.users.dto.UserResponseDTO;
import com.campingmanager.users.entity.User;
import com.campingmanager.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getMe(currentUser));
    }

    @PatchMapping("/avatar")
    public ResponseEntity<UserResponseDTO> updateAvatar(@AuthenticationPrincipal User currentUser,
                                                        @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(currentUser, file));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal User currentUser,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser, request);
        return ResponseEntity.noContent().build();
    }
}
