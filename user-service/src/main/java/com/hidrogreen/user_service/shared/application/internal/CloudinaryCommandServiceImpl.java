package com.hidrogreen.user_service.shared.application.internal;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hidrogreen.user_service.shared.domain.services.CloudinaryCommandService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    // method to convert multipart file to map
    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename())); // se crea un archivo con el nombre del archivo original
        FileOutputStream fo = new FileOutputStream(file); // se crea un flujo de salida para escribir en el archivo
        fo.write(multipartFile.getBytes()); // se escribe en el archivo los bytes del archivo original
        fo.close(); // se cierra el flujo de salida
        return file; // se retorna el archivo
    }

    // method to upload image
    @Override
    public Map uploadImage(MultipartFile multipartFile) throws IOException {
        File file = convert(multipartFile); // se convierte el archivo
        Map result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap()); // se sube el archivo a cloudinary

        if (!Files.deleteIfExists(file.toPath())) { // si no se puede borrar el archivo
            throw new IOException("Error while deleting file" + file.getAbsolutePath()); // se lanza una excepción
        }

        return result; // se retorna el resultado
    }

    // method to delete image
    @Override
    public Map deleteImage(String imageId) throws IOException {
        return cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap()); // se elimina la imagen de cloudinary
    }
}
