import logging
from typing import List

from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from ..queries.get_diagnosis_by_profile_query import GetDiagnosisByProfileQuery
from ..dtos.diagnosis_dto import DiagnosisDTO

logger = logging.getLogger(__name__)

class GetDiagnosisByProfileHandler:
    """Manejador para la consulta GetDiagnosisByProfileQuery"""
    
    def __init__(self, diagnosis_repository: DiagnosisRepository):
        self.diagnosis_repository = diagnosis_repository
    
    async def handle(self, query: GetDiagnosisByProfileQuery) -> List[DiagnosisDTO]:
        """
        Maneja la consulta para obtener diagnósticos de un perfil
        
        Args:
            query: Consulta con el ID del perfil
            
        Returns:
            Lista de DiagnosisDTO
        """
        try:
            diagnoses = await self.diagnosis_repository.find_by_profile_id(query.profile_id)
            
            diagnosis_dtos = [DiagnosisDTO.from_entity(diagnosis) for diagnosis in diagnoses]
            
            logger.info(f"Diagnósticos obtenidos para perfil {query.profile_id}: {len(diagnosis_dtos)}")
            return diagnosis_dtos
            
        except Exception as e:
            logger.error(f"Error al obtener diagnósticos por perfil: {e}")
            raise 