package com.angel.barcatcher.repository

import com.angel.barcatcher.api.Model.Drinkbar
import com.angel.barcatcher.api.RetrofitService
import retrofit2.Response

class barDrinkRepository(service: RetrofitService) {
    var source = service

    suspend fun getDrink(campo: String, valor: String): Response<Drinkbar> =
        source.getBarDrink(campo, valor)

    suspend fun getAllDrink(): Response<List<Drinkbar>> = source.getAllBarDrink()

    suspend fun getDrinkByCoords(
        lat: Double,
        long: Double
    ): Response<List<Drinkbar>> =
        source.getDrinkByCoords(lat, long)
}