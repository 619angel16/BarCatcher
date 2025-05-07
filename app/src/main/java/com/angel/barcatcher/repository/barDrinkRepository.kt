package com.angel.barcatcher.repository

import com.angel.barcatcher.api.Model.DrinkBarRemoteList
import com.angel.barcatcher.api.Model.DrinkBarRemoteResult
import com.angel.barcatcher.api.RetrofitService
import retrofit2.Response

class barDrinkRepository (service: RetrofitService) {
    var source = service

    suspend fun getDrink(ID : String): Response<DrinkBarRemoteResult> = source.getBarDrink(ID)

    suspend fun getAllDrink(): Response<DrinkBarRemoteList> = source.getAllBarDrink()

    //TODO IMPL suspend fun getDrinkByCoords(lat: Float, long : Float): CafeBarRemoteResult = source.getBarCafe()
}