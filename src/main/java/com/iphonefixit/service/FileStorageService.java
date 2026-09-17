package com.iphonefixit.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(
            @Value("${file.upload-dir:uploads}")
            String uploadDir) {

        uploadPath =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();

        try {

            Files.createDirectories(uploadPath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not create upload directory",
                    e
            );
        }
    }

    public String storeFile(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String original =
                file.getOriginalFilename();

        String extension = "";

        if (original != null &&
                original.contains(".")) {

            extension =
                    original.substring(
                            original.lastIndexOf(".")
                    );
        }

        String fileName =
                UUID.randomUUID()
                        + extension;

        try {

            Path target =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption
                            .REPLACE_EXISTING
            );

            return "/uploads/" + fileName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to save phone image",
                    e
            );
        }
    }
}