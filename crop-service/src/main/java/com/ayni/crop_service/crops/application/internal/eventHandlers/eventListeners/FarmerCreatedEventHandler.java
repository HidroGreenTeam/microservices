package com.ayni.crop_service.crops.application.internal.eventHandlers.eventListeners;


import com.ayni.crop_service.crops.domain.model.entities.FarmerRegistry;
import com.ayni.crop_service.crops.domain.model.events.CropsCreatedEvent;
import com.ayni.crop_service.crops.infrastructure.persistence.jpa.repositories.FarmerRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class FarmerCreatedEventHandler {

    private final FarmerRegistryRepository farmerRegistryRepository;

    @Autowired
    public FarmerCreatedEventHandler(FarmerRegistryRepository farmerRegistryRepository) {
        this.farmerRegistryRepository = farmerRegistryRepository;
    }

    @EventListener
    public void handleCropsCreatedEvent(CropsCreatedEvent event) {
        Long farmerId = event.getFarmerId();

        FarmerRegistry farmerRegistry = new FarmerRegistry(farmerId);
        farmerRegistryRepository.save(farmerRegistry);

        System.out.println("Received CropsCreatedEvent for farmerId: " + event.getFarmerId() +
                ", cropName: " + event.getCropName() +
                ", email: " + event.getEmail());
    }
}