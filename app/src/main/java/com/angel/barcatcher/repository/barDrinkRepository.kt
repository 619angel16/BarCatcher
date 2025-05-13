package com.angel.barcatcher.repository

import com.angel.barcatcher.api.Model.CafeBarRemoteList
import com.angel.barcatcher.api.Model.DrinkBarRemoteList
import com.angel.barcatcher.api.Model.DrinkBarRemoteResult
import com.angel.barcatcher.api.RetrofitService
import retrofit2.Response

class barDrinkRepository (service: RetrofitService) {
    var source = service

    suspend fun getDrink(ID : String): Response<DrinkBarRemoteResult> = source.getBarDrink(ID)

    suspend fun getAllDrink(): Response<DrinkBarRemoteList> = source.getAllBarDrink()

    private fun buildQuery(lat: Double, long: Double): String {
        return "from \"Drinkbars\" where spatial.within(spatial.point(latitude, longitude), spatial.circle(0.28, $lat, $long))"
    }
    suspend fun getDrinkByCoords(lat: Double, long: Double): Response<DrinkBarRemoteList> =
        source.getDrinkByCoords(buildQuery(lat, long))
}