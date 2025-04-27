package com.angel.barcatcher.api.Model

import com.google.gson.annotations.SerializedName

data class Country(
    val name: String,
    @SerializedName("@metadata") val metadata: Metadata
)