package com.angel.barcatcher.api.Model

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("@collection") val collection: String,
    @SerializedName("@change-vector") val changeVector: String,
    @SerializedName("@id") val id: String,
    @SerializedName("@last-modified") val lastModified: String
)
