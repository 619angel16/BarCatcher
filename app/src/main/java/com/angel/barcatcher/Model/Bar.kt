package com.angel.barcatcher.Model

abstract class Bar(
    open val id : String,
    open val name: String,
    open val geolong: Double?,
    open val url: String?,
    open val geolat: Double?,
    open val tel: String,
    open val capacity: Int?,
    open val streetAddress: String,
    open val addressLocality: String,
    open val postalCode: Int,
    open val addressCountry: String
)
