from pydantic import BaseModel, Field

class MetadataDTO(BaseModel):
    collection: str = Field(..., alias="@collection")

    class Config:
        populate_by_name = True