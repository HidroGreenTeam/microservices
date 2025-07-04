package com.ayni.crop_service.shared.application.internal.commandServiceImpl;

import com.ayni.crop_service.shared.domain.services.CloudinaryCommandService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryCommandServiceImpl implements CloudinaryCommandService {

    private final Cloudinary cloudinary;

    public CloudinaryCommandServiceImpl() {

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "deu4nwmqh");
        config.put("api_key", "789752667392435");
        config.put("api_secret", "QlOIvCICBryMf5qy2HryHoJMpUQ");
        cloudinary = new Cloudinary(config);
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fo = new FileOutputStream(file);
        fo.write(multipartFile.getBytes());
        fo.close();
        return file;
    }

    @Override
    public Map uploadImage(MultipartFile multipartFile) throws IOException {
        File file = convert(multipartFile);
        Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());

        if (!Files.deleteIfExists(file.toPath())) {
            throw new IOException("Error while deleting file" + file.getAbsolutePath());
        }

        return result;
    }

    @Override
    public Map deleteImage(String imageId) throws IOException {
        return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
    }
}
