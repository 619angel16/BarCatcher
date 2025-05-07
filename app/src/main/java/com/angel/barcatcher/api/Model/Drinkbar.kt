package com.angel.barcatcher.api.Model

import com.google.gson.annotations.SerializedName

data class Drinkbar(
    val name: String,
    val geo_long: Double?,
    val url: String?,
    val geo_lat: Double?,
    val email: String?,
    val tel: String?,
    val rdfs_label__1: String?,
    val capacity: String?,
    val hasFood: String?,
    val address_streetAddress: String,
    val address_addressLocality: String,
    val address_addressCountry: String,
    val address_postalCode: Int,
    @SerializedName("@metadata") val metadata: Metadata
)
