package com.campingmanager.users;

import com.campingmanager.exceptions.BadRequestException;
import com.campingmanager.users.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService(tempDir.toString());
    }

    @Test
    void shouldStoreFileAndReturnPublicPath() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "fake-image-bytes".getBytes());

        String path = fileStorageService.store(file);

        assertThat(path).startsWith("/uploads/");
        assertThat(path).endsWith(".png");
        String storedName = path.substring("/uploads/".length());
        assertThat(Files.exists(tempDir.resolve(storedName))).isTrue();
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile empty = new MockMultipartFile(
                "file", "empty.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> fileStorageService.store(empty))
                .isInstanceOf(BadRequestException.class);
    }
}
