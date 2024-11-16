package com.example.flowerstore.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadImageFile {
    String uploadImage(MultipartFile file) throws IOException;

    String uploadOverwriteImage(MultipartFile file, String publicId) throws IOException;

    String deleteImage(String publicId) throws IOException;
}
