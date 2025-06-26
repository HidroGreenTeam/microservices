package com.ayni.notification_service.notifications.application.internal.services;

import com.ayni.notification_service.notifications.application.internal.outboundservices.acl.ExternalProfileService;
import com.ayni.notification_service.notifications.domain.model.valueobjects.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for converting between farmerId/agronomistId and userId
 */
@Service
public class UserConversionService {
    
    private static final Logger log = LoggerFactory.getLogger(UserConversionService.class);
    
    private final ExternalProfileService externalProfileService;

    public UserConversionService(ExternalProfileService externalProfileService) {
        this.externalProfileService = externalProfileService;
    }

    /**
     * Convierte farmerId a userId consultando el user-service
     * @param farmerId ID del farmer
     * @return userId correspondiente
     */
    public Long convertFarmerIdToUserId(Long farmerId) {
        try {
            // Aquí deberías llamar al user-service para obtener el userId del farmer
            // Por ahora, asumimos que farmerId = userId para compatibilidad
            log.debug("Converting farmerId {} to userId", farmerId);
            
            // TODO: Implementar llamada real al user-service
            // return userServiceClient.getUserIdByFarmerId(farmerId);
            
            // Retorna farmerId por ahora para mantener compatibilidad
            return farmerId;
            
        } catch (Exception e) {
            log.error("Error converting farmerId {} to userId: {}", farmerId, e.getMessage());
            throw new RuntimeException("Failed to convert farmerId to userId", e);
        }
    }

    /**
     * Convierte userId a farmerId (operación inversa)
     * @param userId ID del usuario
     * @return farmerId correspondiente
     */
    public Long convertUserIdToFarmerId(Long userId) {
        try {
            log.debug("Converting userId {} to farmerId", userId);
            
            // TODO: Implementar llamada real al user-service
            // return userServiceClient.getFarmerIdByUserId(userId);
            
            // Retorna userId por ahora para mantener compatibilidad
            return userId;
            
        } catch (Exception e) {
            log.error("Error converting userId {} to farmerId: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to convert userId to farmerId", e);
        }
    }

    /**
     * Determina el UserRole basado en si existe como farmer o agronomist
     * @param userId ID del usuario
     * @return UserRole correspondiente
     */
    public UserRole determineUserRole(Long userId) {
        try {
            // TODO: Implementar lógica real consultando user-service
            // Por ahora asume que todos son FARMER
            log.debug("Determining role for userId {}", userId);
            
            // Verificar si existe como farmer
            boolean isFarmer = externalProfileService.farmerExists(userId);
            if (isFarmer) {
                return UserRole.FARMER;
            }
            
            // TODO: Verificar si existe como agronomist cuando se implemente
            // boolean isAgronomist = externalProfileService.agronomistExists(userId);
            // if (isAgronomist) {
            //     return UserRole.AGRONOMIST;
            // }
            
            // Por defecto, asumir FARMER
            return UserRole.FARMER;
            
        } catch (Exception e) {
            log.error("Error determining role for userId {}: {}", userId, e.getMessage());
            return UserRole.FARMER; // Default fallback
        }
    }
} 