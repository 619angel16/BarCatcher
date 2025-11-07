from pydantic import BaseModel

class LocationDTO(BaseModel):
    longitude: float
    latitude: float
    
