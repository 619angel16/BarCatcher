from repository.GenericRespository import BaseRepository
from dto.CafedrinksDTO import CafedrinksDTO
from helpers.connectBar_CC import RavenDBClient
from typing import List, Optional

class CafedrinksRepository(BaseRepository[CafedrinksDTO]):

    def __init__(self, client: RavenDBClient):
        super().__init__(client, "Drinkbars", CafedrinksDTO)


    def find_by_locality(self, locality: str) -> Optional[List[CafedrinksDTO]]:
        return self.find_by_field("address.locality", locality)
    

    def find_by_email(self, email: str) -> Optional[List[CafedrinksDTO]]:
        return self.find_by_field("email", email)
    
    def find_who_serves_food(self) -> Optional[List[CafedrinksDTO]]:
        return self.find_by_field("servesFood", True)