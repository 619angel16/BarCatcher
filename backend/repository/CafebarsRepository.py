from repository.GenericRespository import BaseRepository
from dto.CafebarsDTO import CafebarsDTO
from helpers.connectBar_CC import RavenDBClient
from typing import Optional, List

class CafebarsRepository(BaseRepository[CafebarsDTO]):

    def __init__(self, client: RavenDBClient):
        super().__init__(client, "Cafebars", CafebarsDTO)

    def find_by_street(self, street: str) -> Optional[List[CafebarsDTO]]:
        return self.find_by_field("address.street", street)
    
    def find_by_capacity(self, capacity: int) -> Optional[List[CafebarsDTO]]:
        return self.find_by_field("capacity", capacity)