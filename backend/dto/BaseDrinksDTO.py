from pydantic import BaseModel, Field
from typing import Optional
from dto.AddressDTO import AddressDTO
from dto.LocationDTO import LocationDTO
from dto.MetadataDTO import MetadataDTO

class BaseDrinksDTO(BaseModel):
    id: MetadataDTO = Field(None, alias = "@id")
    name: str
    location: LocationDTO
    url: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None
    capacity: Optional[str] = None
    servesFood: Optional[bool] = None
    address: AddressDTO
    metadata: MetadataDTO = Field(..., alias="@metadata")

    class Config:
        populate_by_name = True