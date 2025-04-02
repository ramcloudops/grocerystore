package com.turmericstore.service;

import com.google.cloud.storage.*;
import com.turmericstore.exception.BadRequestException;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StorageService {

    private final Storage storage;

    @Value("${app.storage.bucket-name}")
    private String bucketName;

    @Autowired
    public StorageService(Storage storage) {
        this.storage = storage;
    }

    public String uploadProductImage(MultipartFile file) {
        return uploadFile(file, AppConstants.PRODUCT_IMAGES_PATH);
    }

    public String uploadCategoryImage(MultipartFile file) {
        return uploadFile(file, AppConstants.CATEGORY_IMAGES_PATH);
    }

    public List<String> uploadProductImages(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            String imageUrl = uploadProductImage(file);
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }

    public void deleteFile(String fileUrl) {
        try {
            // Extract file path from URL
            String filePath = extractFilePathFromUrl(fileUrl);

            // Delete file from storage
            BlobId blobId = BlobId.of(bucketName, filePath);
            boolean deleted = storage.delete(blobId);

            if (!deleted) {
                throw new BadRequestException("Failed to delete file: " + filePath);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid file URL: " + fileUrl);
        }
    }

    // Helper methods
    private String uploadFile(MultipartFile file, String folder) {
        try {
            // Validate file
            validateFile(file);

            // Generate unique file name
            String fileName = generateFileName(file.getOriginalFilename());

            // Create file path
            String filePath = folder + "/" + fileName;

            // Upload file to storage
            BlobId blobId = BlobId.of(bucketName, filePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            // Upload content
            storage.create(blobInfo, file.getBytes());

            // Generate public URL
            return generatePublicUrl(filePath);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }

        // Check file size (5MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BadRequestException("File size exceeds the maximum limit (5MB)");
        }
    }

    private String generateFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + fileExtension;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }

    private String generatePublicUrl(String filePath) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, filePath);
    }

    private String extractFilePathFromUrl(String fileUrl) {
        String storageBaseUrl = "https://storage.googleapis.com/" + bucketName + "/";

        if (!fileUrl.startsWith(storageBaseUrl)) {
            throw new IllegalArgumentException("Invalid file URL, doesn't match our storage bucket");
        }

        return fileUrl.substring(storageBaseUrl.length());
    }
}