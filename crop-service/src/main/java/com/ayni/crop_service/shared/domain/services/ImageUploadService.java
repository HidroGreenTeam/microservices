package com.ayni.crop_service.shared.domain.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Image upload service interface
 */
public interface ImageUploadService {
    
    /**
     * Upload an image and return the URL
     * @param file the image file to upload
     * @param folder the folder to upload to (e.g., "crops")
     * @return the URL of the uploaded image
     * @throws Exception if upload fails
     */
    String uploadImage(MultipartFile file, String folder) throws Exception;
    
    /**
     * Delete an image by URL
     * @param imageUrl the URL of the image to delete
     * @return true if deletion was successful
     */
    boolean deleteImage(String imageUrl);
}
