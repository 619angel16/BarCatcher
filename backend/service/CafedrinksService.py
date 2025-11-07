from service.GenericService import BaseService
from dto.CafedrinksDTO import CafedrinksDTO
from repository.CafedrinksRepository import CafedrinksRepository
from typing import List, Optional

class CafedrinksService(BaseService[CafedrinksDTO]):
    def __init__(self, repository : CafedrinksRepository):
        super().__init__(repository)
    
    def get_by_locality(self, locality : str) -> Optional[List[CafedrinksDTO]]:
        return self.repository.find_by_locality(locality)
    
    def get_by_email(self, email : str) -> Optional[List[CafedrinksDTO]]:
        return self.repository.find_by_email(email)
    
    def get_who_serves_food(self):
        return self.repository.find_who_serves_food()