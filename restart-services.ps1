$services = @(
    "discovery-service",
    "api-gateway",
    "crop-service",
    "detection-service",
    "notification-service",
    "paymet-gateway-service",
    "report-service",
    "subscription-service",
    "treatment-service",
    "user-service"
)

$resourceGroup = "hidrogreenteam"
$acrName = "aynicontainer.azurecr.io"

foreach ($service in $services) {
    Write-Host "=== Procesando $service ==="
    
    # Verificar si existe el directorio del servicio
    if (-not (Test-Path "./$service")) {
        Write-Host "ERROR: No se encontró el directorio ./$service"
        continue
    }
    
    # Verificar si existe Dockerfile
    if (-not (Test-Path "./$service/Dockerfile")) {
        Write-Host "ERROR: No se encontró Dockerfile en ./$service"
        continue
    }
    
    # Formar el tag completo
    $imageTag = "$acrName/$service`:latest"
    Write-Host "Tag de imagen: $imageTag"
    
    Write-Host "Construyendo imagen para $service..."
    try {
        # Construir la imagen Docker
        $buildResult = docker build -t $imageTag "./$service" 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Falló el build de $service"
            Write-Host $buildResult
            continue
        }
        Write-Host "Imagen de $service construida exitosamente."
    }
    catch {
        Write-Host ("ERROR: Error al construir la imagen de {0}: {1}" -f $service, $_.Exception.Message)
        continue
    }
    
    Write-Host "Subiendo imagen para $service..."
    try {
        # Subir la imagen al ACR
        $pushResult = docker push $imageTag 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Falló el push de $service"
            Write-Host $pushResult
            continue
        }
        Write-Host "Imagen de $service subida exitosamente."
    }
    catch {
        Write-Host ("ERROR: Error al subir la imagen de {0}: {1}" -f $service, $_.Exception.Message)
        continue
    }

    Write-Host "Actualizando container app para $service..."
    try {
        $updateResult = az containerapp update `
            --name $service `
            --resource-group $resourceGroup `
            --image $imageTag `
            --output none 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host "ERROR: Falló la actualización de $service"
            Write-Host $updateResult
            continue
        }
        Write-Host "$service actualizado exitosamente."
    }
    catch {
        Write-Host ("ERROR: Error al actualizar el container app de {0}: {1}" -f $service, $_.Exception.Message)
        continue
    }

    Write-Host "Reiniciando $service..."
    try {
        # Obtener la revisión activa
        $revision = az containerapp revision list `
            --name $service `
            --resource-group $resourceGroup `
            --query "[?active==true].name | [0]" `
            --output tsv

        if (-not $revision) {
            Write-Host "WARNING: No se encontró una revisión activa para $service"
            continue
        }

        # Reiniciar la revisión
        Write-Host "Reiniciando revisión: $revision"
        $restartResult = az containerapp revision restart `
            --name $service `
            --resource-group $resourceGroup `
            --revision $revision `
            --output none 2>&1
        if ($LASTEXITCODE -ne 0) {
            Write-Host "WARNING: Falló el reinicio de $service"
            Write-Host $restartResult
        } else {
            Write-Host "$service reiniciado exitosamente."
        }
    }
    catch {
        Write-Host ("ERROR: Error al reiniciar {0}: {1}" -f $service, $_.Exception.Message)
    }
    
    Write-Host "=== $service completado ==="
    Write-Host ""
}
