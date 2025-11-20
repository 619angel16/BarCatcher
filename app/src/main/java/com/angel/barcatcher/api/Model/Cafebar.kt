package com.angel.barcatcher.api.Model

import com.google.gson.annotations.SerializedName

data class Cafebar(
    val name: String,
    val url: String?,
    val email: String?,
    val phone: String?,
    val capacity: String?,
    val address: Address,
    val location: Location?,
    @SerializedName("metadata") val metadata: Metadata
)
