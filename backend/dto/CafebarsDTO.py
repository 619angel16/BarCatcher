from dto.BaseDrinksDTO import BaseDrinksDTO
from pydantic import Field
from typing import Optional
from dto.AddressDTO import AddressDTO
from dto.LocationDTO import LocationDTO
from dto.MetadataDTO import MetadataDTO

class CafebarsDTO(BaseDrinksDTO):
    name: str
    location: LocationDTO
    url: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None
    capacity: Optional[str] = None
    address: AddressDTO
    metadata: MetadataDTO = Field(..., alias="@metadata")

    class Config:
        populate_by_name = True