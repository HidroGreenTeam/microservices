package com.ayni.crop_service.shared.infrastructure.services;

import com.ayni.crop_service.shared.domain.services.ImageUploadService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Cloudinary implementation of the image upload service
 */
@Service
public class CloudinaryImageUploadService implements ImageUploadService {

    private final Cloudinary cloudinary;

    public CloudinaryImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile file, String folder) throws Exception {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }

            // Generate unique filename
            String publicId = folder + "/" + UUID.randomUUID().toString();

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", "image",
                    "quality", "auto:good",
                    "fetch_format", "auto"
                )
            );

            // Return the secure URL
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new Exception("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return false;
            }

            // Extract public ID from URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) {
                return false;
            }

            // Delete from Cloudinary
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));

        } catch (Exception e) {
            // Log the error but don't throw exception
            System.err.println("Failed to delete image: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * @param imageUrl the Cloudinary image URL
     * @return the public ID or null if extraction fails
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{transformations}/{public_id}.{format}
            if (!imageUrl.contains("cloudinary.com")) {
                return null;
            }

            String[] parts = imageUrl.split("/");
            if (parts.length < 7) {
                return null;
            }

            // Find the upload part and get everything after it
            for (int i = 0; i < parts.length; i++) {
                if ("upload".equals(parts[i]) && i + 1 < parts.length) {
                    StringBuilder publicId = new StringBuilder();
                    for (int j = i + 1; j < parts.length; j++) {
                        if (j > i + 1) {
                            publicId.append("/");
                        }
                        publicId.append(parts[j]);
                    }
                    // Remove file extension
                    String result = publicId.toString();
                    int lastDot = result.lastIndexOf('.');
                    if (lastDot > 0) {
                        result = result.substring(0, lastDot);
                    }
                    return result;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
