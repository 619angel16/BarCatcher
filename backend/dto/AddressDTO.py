from pydantic import BaseModel

class AddressDTO(BaseModel):
    street: str
    locality: str
    country: str
    postalCode: int