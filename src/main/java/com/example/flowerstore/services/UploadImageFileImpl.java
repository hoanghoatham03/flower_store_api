package com.example.flowerstore.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadImageFileImpl implements UploadImageFile {

    private final Cloudinary cloudinary;

    public static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String publicValue = generatePublicValue(file.getOriginalFilename());
        log.info("Generated public value: {}", publicValue);

        String extension = getFileExtension(file.getOriginalFilename());
        log.info("File extension: {}", extension);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", publicValue));
            log.info("Upload result: {}", uploadResult);
            return generateFileUrl(publicValue, extension);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String uploadOverwriteImage(MultipartFile file, String publicId) throws IOException {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        log.info("File extension: {}", extension);
        //delete old image and upload new image
        deleteImage(publicId);
        String newPublicId = generatePublicValue(file.getOriginalFilename());
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", newPublicId));
            log.info("Upload result: {}", uploadResult);
            return generateFileUrl(newPublicId, extension);
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String deleteImage(String publicId) throws IOException {
        try {
            Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Delete result: {}", deleteResult);
            return deleteResult.get("result").toString();
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw e;
        }
    }


    private String generateFileUrl(String publicValue, String extension) {
        return cloudinary.url().generate(publicValue + "." + extension);
    }

    private String generatePublicValue(String originalName) {
        String fileName = getFileNameWithoutExtension(originalName);
        return UUID.randomUUID().toString() + "_" + fileName;
    }

    private String getFileNameWithoutExtension(String originalName) {
        int lastDotIndex = originalName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return originalName;
        }
        return originalName.substring(0, lastDotIndex);
    }

    private String getFileExtension(String originalName) {
        int lastDotIndex = originalName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalName.length() - 1) {
            throw new IllegalArgumentException("Invalid file extension for: " + originalName);
        }
        String extension = originalName.substring(lastDotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Invalid file extension for: " + originalName);
        }
        return extension;
    }
}