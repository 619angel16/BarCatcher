from pydantic import BaseModel, Field, AliasChoices
from typing import Optional
from dto.AddressDTO import AddressDTO
from dto.LocationDTO import LocationDTO
from dto.MetadataDTO import MetadataDTO

class BaseDrinksDTO(BaseModel):
    id: str = Field(..., validation_alias=AliasChoices("@id", "Id"))
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