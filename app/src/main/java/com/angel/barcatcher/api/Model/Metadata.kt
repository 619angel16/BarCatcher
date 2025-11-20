package com.angel.barcatcher.api.Model

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("collection") val collection: String,
    @SerializedName("id") val id: String,

)
