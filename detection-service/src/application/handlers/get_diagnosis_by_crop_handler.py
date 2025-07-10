import logging
from typing import List

from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from ..queries.get_diagnosis_by_crop_query import GetDiagnosisByCropQuery
from ..dtos.diagnosis_dto import DiagnosisDTO

logger = logging.getLogger(__name__)

class GetDiagnosisByCropHandler:
    """Manejador para la consulta GetDiagnosisByCropQuery"""
    
    def __init__(self, diagnosis_repository: DiagnosisRepository):
        self.diagnosis_repository = diagnosis_repository
    
    async def handle(self, query: GetDiagnosisByCropQuery) -> List[DiagnosisDTO]:
        """
        Maneja la consulta para obtener diagnósticos de un cultivo
        
        Args:
            query: Consulta con el ID del cultivo
            
        Returns:
            Lista de DiagnosisDTO
        """
        try:
            diagnoses = await self.diagnosis_repository.find_by_crop_id(query.crop_id)
            
            diagnosis_dtos = [DiagnosisDTO.from_entity(diagnosis) for diagnosis in diagnoses]
            
            logger.info(f"Diagnósticos obtenidos para cultivo {query.crop_id}: {len(diagnosis_dtos)}")
            return diagnosis_dtos
            
        except Exception as e:
            logger.error(f"Error al obtener diagnósticos por cultivo: {e}")
            raise 