package com.angel.barcatcher.api.Model

data class DrinkBarRemoteResult(
    val Results: List<Drinkbar>,
    val Includes: Map<String, Any>
)
