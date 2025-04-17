package com.angel.barcatcher.Model

import com.angel.barcatcher.Model.Bar

data class DrinkBar(
    override val id: String,
    override val name: String,
    override val geolong: Double?,
    override val url: String?,
    override val geolat: Double?,
    override val tel: String,
    override val capacity: Int?,
    override val streetAddress: String,
    override val addressLocality: String,
    override val postalCode: Int,
    override val addressCountry: String
) : Bar(
    id, name, geolong, url, geolat, tel, capacity,
    streetAddress, addressLocality, postalCode, addressCountry
)
