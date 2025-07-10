import logging
from typing import Optional

from ...domain.repositories.diagnosis_repository import DiagnosisRepository
from ..queries.get_diagnosis_by_id_query import GetDiagnosisByIdQuery
from ..dtos.diagnosis_dto import DiagnosisDTO

logger = logging.getLogger(__name__)

class GetDiagnosisByIdHandler:
    """Manejador para la consulta GetDiagnosisByIdQuery"""
    
    def __init__(self, diagnosis_repository: DiagnosisRepository):
        self.diagnosis_repository = diagnosis_repository
    
    async def handle(self, query: GetDiagnosisByIdQuery) -> Optional[DiagnosisDTO]:
        """
        Maneja la consulta para obtener un diagnóstico por ID
        
        Args:
            query: Consulta con el ID del diagnóstico
            
        Returns:
            DiagnosisDTO si se encuentra, None en caso contrario
        """
        try:
            diagnosis = await self.diagnosis_repository.find_by_id(query.diagnosis_id)
            
            if not diagnosis:
                logger.warning(f"Diagnóstico no encontrado: {query.diagnosis_id}")
                return None
            
            diagnosis_dto = DiagnosisDTO.from_entity(diagnosis)
            logger.info(f"Diagnóstico obtenido: {query.diagnosis_id}")
            return diagnosis_dto
            
        except Exception as e:
            logger.error(f"Error al obtener diagnóstico por ID: {e}")
            raise 