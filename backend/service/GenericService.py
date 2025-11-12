from typing import TypeVar, Generic, List, Optional, Type, Dict, Any
from repository.GenericRespository import BaseRepository
from pydantic import BaseModel

T = TypeVar('T', bound=BaseModel)

class BaseService(Generic[T]):
    
    def __init__(self, repository: BaseRepository[T]):
        self.repository = repository

    def find_by_public_id(self, publicId: str) -> Optional[T]:
        return self.repository.find_by_id(publicId)

    def get_all(self, limit: Optional[int]) -> Optional[List[T]]:
        return self.repository.find_all(limit)  
    
    def get_by_cercania(self, latitud, longitud, radio) -> Optional[List[T]]:
        return self.repository.find_by_cercania(latitud, longitud, radio)
    
    def create(self, data: Dict[str, Any]) -> str:
        return self.repository.save(data)
    
    def delete(self, entity_id: str) -> bool:
        return self.repository.delete(entity_id)
    
    def encontrar_por_campo(self, field_name: str, field_value: Any) -> Optional[T]:
        return self.repository.find_by_field(field_name, field_value)
    
    def existe(self, entidad_id: str) -> bool:
        return self.repository.existe(entidad_id)

    
