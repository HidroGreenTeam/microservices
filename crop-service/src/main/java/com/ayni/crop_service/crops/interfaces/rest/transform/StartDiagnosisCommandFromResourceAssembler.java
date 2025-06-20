package com.ayni.crop_service.crops.interfaces.rest.transform;

import com.ayni.crop_service.crops.domain.model.commands.StartDiagnosisCommand;
import com.ayni.crop_service.crops.interfaces.rest.resources.StartDiagnosisResource;

/**
 * Assembler to convert StartDiagnosisResource to StartDiagnosisCommand
 */
public class StartDiagnosisCommandFromResourceAssembler {

    public static StartDiagnosisCommand toCommandFromResource(StartDiagnosisResource resource) {
        return new StartDiagnosisCommand(
            resource.cropId(),
            resource.profileId(),
            resource.imageUrl()
        );
    }
}
