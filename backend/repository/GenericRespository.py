from typing import TypeVar, Generic, List, Optional, Type, Dict, Any
from pydantic import BaseModel
from helpers.connectBar_CC import RavenDBClient, _client

T = TypeVar('T', bound=BaseModel)

class BaseRepository(Generic[T]):
    """ Repositorio Genérico para cualquier entidad """

    def __init__(self, client : RavenDBClient, collection_name: str, dto_class: Type[T]):
        self.client = client
        self.collection_name = collection_name
        self.dto_class = dto_class

    def _generate_id(self, entity_id : Optional[str] = None) -> str:
        if entity_id:

            if '/' in entity_id: # Si ya contienen el formato collection/id, no hay nada que hacer
                return entity_id

            return f"{self.collection_name}/{entity_id}" # Añadimos el formato collection/id
        
        return None # Si no se proporciona una id, no se genera la id

    def find_by_id(self, requestId: str) -> Optional[T]:

        full_id = self._generate_id(requestId)
        doc = self.client.get_document(full_id)

        if doc:
            if 'Id' not in doc and '@metadata' in doc:
                doc['Id'] = doc['@metadata'].get('@id', full_id)

            return self.dto_class(**doc)
        return None
    
        
    def find_all(self, limit : Optional[int]) -> Optional[List[T]]:
        
        query = f"FROM {self.collection_name} LIMIT {limit}" if limit else f"FROM {self.collection_name}"   
        results = self.client.query_documents(query)
        print(results)
        if results and 'Results' in results:
            entities = []
            print('entro')
            for doc in results['Results']:
                if 'Id' not in doc and '@metadata' in doc:
                    doc['Id'] = doc['@metadata'].get('@id')
                entities.append(self.dto_class(**doc))    

            return entities
        return []    
    
    def find_by_field(self, field_name: str, field_value: Any, limit : int = 100) -> Optional[List[T]]:
        
        # Ajustar valores RQL
        if isinstance(field_value, str):
            field_value = f"'{field_value}'"
        if isinstance(field_value, bool):
            field_value = str(field_value).lower()

        query = f"""
            FROM {self.collection_name} WHERE {field_name} = {field_value} LIMIT {limit}
        """        

        results = self.client.query_documents(query)

        if results and 'Results' in results:
            for doc in results:
                entidades = []
                if 'Id' not in doc and '@metadata' in doc:
                    doc['Id'] = doc['@metadata'].get('@id')
                entidades.append(self.dto_class(**doc))
            return entidades
        return None        

                    

    def find_by_cercania(self, latitud: float, longitud: float, radio: float) -> Optional[List[T]]:
       
        query = f"""FROM {self.collection_name} WHERE spatial.within(
            spatial.point(location.longitude, location.latitude),
            spatial.circle({radio}, {latitud}, {longitud}, 'kilometers')
        )"""

        results = self.client.query_documents(query)

        if results and 'Results' in results:
            entidades = []
            for doc in results:
                if 'Id' not in doc and '@metadata' in doc:
                    doc['Id'] = doc['@metadata'].get('@id')
                entidades.append(self.dto_class(**doc))
            return entidades
        return []        
        
    
    def existe(self, public_id: str) -> bool:
        return self.find_by_id(public_id) is not None

    def save(self, entity_id, data: Dict[str, any]) -> str:
        full_id = self._generate_id(entity_id)
        return self.client.save_document(entity_id, data)
        
    def delete(self, entity_id: str) -> bool:
        full_id = self._generate_id(entity_id)
        return self.client.delete_document(entity_id)