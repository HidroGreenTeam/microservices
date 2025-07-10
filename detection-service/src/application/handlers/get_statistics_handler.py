import logging

from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from ..queries.get_statistics_query import GetStatisticsQuery
from ..dtos.statistics_dto import StatisticsDTO

logger = logging.getLogger(__name__)

class GetStatisticsHandler:
    """Manejador para la consulta GetStatisticsQuery"""
    
    def __init__(self, diagnosis_repository: DiagnosisRepository):
        self.diagnosis_repository = diagnosis_repository
    
    async def handle(self, query: GetStatisticsQuery) -> StatisticsDTO:
        """
        Maneja la consulta para obtener estadísticas
        
        Args:
            query: Consulta de estadísticas
            
        Returns:
            StatisticsDTO con las estadísticas
        """
        try:
            total_diagnoses = await self.diagnosis_repository.count_total_diagnoses()
            diseases_detected = await self.diagnosis_repository.count_diseases_detected()
            treatments_required = await self.diagnosis_repository.count_requiring_treatment()
            disease_distribution = await self.diagnosis_repository.get_disease_statistics()
            
            statistics_dto = StatisticsDTO(
                total_diagnoses=total_diagnoses,
                diseases_detected=diseases_detected,
                treatments_required=treatments_required,
                healthy_crops=total_diagnoses - diseases_detected,
                disease_distribution=disease_distribution
            )
            
            logger.info("Estadísticas obtenidas exitosamente")
            return statistics_dto
            
        except Exception as e:
            logger.error(f"Error al obtener estadísticas: {e}")
            raise 