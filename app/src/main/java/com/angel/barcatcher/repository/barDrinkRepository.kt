package com.angel.barcatcher.repository

import com.angel.barcatcher.api.Model.DrinkBarRemoteList
import com.angel.barcatcher.api.Model.DrinkBarRemoteResult
import com.angel.barcatcher.api.Model.Drinkbar
import com.angel.barcatcher.api.RetrofitService
import retrofit2.Response

class barDrinkRepository(service: RetrofitService) {
    var source = service

    suspend fun getDrink(ID: String): Response<DrinkBarRemoteResult> = source.getBarDrink(ID)

    suspend fun getAllDrink(): Response<DrinkBarRemoteList> = source.getAllBarDrink()

    private fun buildQuery(lat: Double, long: Double): String {
        return "latitud=$lat&longitud=$long&radio=0.28"
    }

    suspend fun getDrinkByCoords(
        lat: Double,
        long: Double,
        radius: Float
    ): Response<List<Drinkbar>> =
        source.getDrinkByCoords(lat, long, radius)
}