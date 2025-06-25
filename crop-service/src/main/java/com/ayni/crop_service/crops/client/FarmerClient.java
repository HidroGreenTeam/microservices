package com.ayni.crop_service.crops.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class FarmerClient {

    private static final Logger logger = LoggerFactory.getLogger(FarmerClient.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;


    public ResponseEntity<Boolean> existsFarmerById(Long farmerId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");
        if (instances == null || instances.isEmpty()) {
            logger.error("No se encontraron instancias de user-service");
            throw new IllegalStateException("No se encontraron instancias de user-service");
        }
        String url = instances.get(0).getUri().toString() + "/api/v1/farmers/exists?id=" + farmerId;
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        } catch (Exception e) {
            logger.error("Error llamando a farmer-service: {}", e.getMessage());
            throw new IllegalStateException("Error conectando a farmer-service", e);
        }
    }


}
