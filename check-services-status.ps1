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

Write-Host "=== ESTADO DE SERVICIOS DE AZURE CONTAINER APPS ===" -ForegroundColor Cyan
Write-Host ""

# Obtener el estado de todos los servicios de una vez
Write-Host "Obteniendo estado de todos los servicios..." -ForegroundColor Yellow
$allServices = az containerapp list `
    --resource-group $resourceGroup `
    --query "[].{Name:name,Status:properties.runningStatus,Revisions:properties.latestRevisionName,ProvisioningState:properties.provisioningState}" `
    --output json | ConvertFrom-Json

Write-Host "=== RESUMEN DE ESTADOS ===" -ForegroundColor Cyan
Write-Host ""

$runningCount = 0
$stoppedCount = 0
$errorCount = 0

foreach ($service in $allServices) {
    $statusColor = switch ($service.Status) {
        "Running" { "Green"; $runningCount++ }
        "Stopped" { "Red"; $stoppedCount++ }
        default { "Yellow"; $errorCount++ }
    }
    
    Write-Host ("{0,-20} | {1,-10} | {2,-15} | {3}" -f $service.Name, $service.Status, $service.ProvisioningState, $service.Revisions) -ForegroundColor $statusColor
}

Write-Host ""
Write-Host "=== ESTADÍSTICAS ===" -ForegroundColor Cyan
Write-Host "Servicios ejecutándose: $runningCount" -ForegroundColor Green
Write-Host "Servicios detenidos: $stoppedCount" -ForegroundColor Red
Write-Host "Servicios con errores: $errorCount" -ForegroundColor Yellow
Write-Host "Total de servicios: $($allServices.Count)" -ForegroundColor White

Write-Host ""
Write-Host "=== DETALLES POR SERVICIO ===" -ForegroundColor Cyan
Write-Host ""

foreach ($serviceName in $services) {
    Write-Host "=== $serviceName ===" -ForegroundColor Yellow
    
    try {
        # Buscar el servicio en la lista
        $service = $allServices | Where-Object { $_.Name -eq $serviceName }
        
        if (-not $service) {
            Write-Host "❌ Servicio no encontrado" -ForegroundColor Red
            continue
        }
        
        # Mostrar información detallada
        Write-Host "Estado: $($service.Status)" -ForegroundColor $(if ($service.Status -eq "Running") { "Green" } else { "Red" })
        Write-Host "Estado de aprovisionamiento: $($service.ProvisioningState)" -ForegroundColor Cyan
        Write-Host "Revisión activa: $($service.Revisions)" -ForegroundColor White
        
        # Obtener información adicional si está ejecutándose
        if ($service.Status -eq "Running") {
            $revisionInfo = az containerapp revision show `
                --name $serviceName `
                --resource-group $resourceGroup `
                --revision $service.Revisions `
                --query "{Replicas:properties.replicas,ActiveReplicas:properties.activeReplicas,ReadyReplicas:properties.readyReplicas}" `
                --output json 2>$null | ConvertFrom-Json
            
            if ($revisionInfo) {
                Write-Host "Réplicas totales: $($revisionInfo.Replicas)" -ForegroundColor White
                Write-Host "Réplicas activas: $($revisionInfo.ActiveReplicas)" -ForegroundColor Green
                Write-Host "Réplicas listas: $($revisionInfo.ReadyReplicas)" -ForegroundColor Green
            }
        }
        
    }
    catch {
        Write-Host ("ERROR: Error al obtener información de {0}: {1}" -f $serviceName, $_.Exception.Message) -ForegroundColor Red
    }
    
    Write-Host ""
}

Write-Host "=== COMANDOS ÚTILES ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para ver logs de un servicio específico:" -ForegroundColor White
Write-Host "az containerapp logs show --name <nombre-servicio> --resource-group $resourceGroup --follow"
Write-Host ""
Write-Host "Para reiniciar un servicio específico:" -ForegroundColor White
Write-Host "az containerapp restart --name <nombre-servicio> --resource-group $resourceGroup"
Write-Host ""
Write-Host "Para ver la configuración de un servicio:" -ForegroundColor White
Write-Host "az containerapp show --name <nombre-servicio> --resource-group $resourceGroup" 