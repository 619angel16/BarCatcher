from pydantic import BaseModel, Field

class MetadataDTO(BaseModel):
    collection: str = Field(..., alias="@collection")
    id: str = Field(..., alias="@id")
    class Config:
        populate_by_name = True