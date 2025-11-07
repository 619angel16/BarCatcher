from service.GenericService import BaseService
from dto.CafebarsDTO import CafebarsDTO
from repository.CafebarsRepository import CafebarsRepository
from typing import Optional, List

class CafebarsService(BaseService[CafebarsDTO]):
    def __init__(self, repository: CafebarsRepository):
        super().__init__(repository)
    
    def get_by_street(self, street: str) -> Optional[List[CafebarsDTO]]:
        return self.repository.find_by_street(street)
    
    def get_by_capacity(self, capacity: int) -> Optional[List[CafebarsDTO]]:
        return self.repository.find_by_capacity(capacity)
