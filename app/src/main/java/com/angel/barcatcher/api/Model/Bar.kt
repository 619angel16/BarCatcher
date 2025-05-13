package com.angel.barcatcher.api.Model

sealed class Bar {
    data class Cafe(val data: Cafebar) : Bar()
    data class Drink(val data: Drinkbar) : Bar()
}