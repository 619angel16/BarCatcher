package com.angel.barcatcher.repository

import com.angel.barcatcher.api.Model.Cafebar
import com.angel.barcatcher.api.RetrofitService
import retrofit2.Response

class barCafeRepository(service: RetrofitService) {
    var source = service

    suspend fun getCafe(campo: String, valor: String): Response<Cafebar> =
        source.getBarCafe(campo, valor)

    suspend fun getAllCafe(): Response<List<Cafebar>> = source.getAllBarCafe()

    suspend fun getCafeByCoords(lat: Double, long: Double): Response<List<Cafebar>> =
        source.getCafeByCoords(lat, long)


    //TODO IMPL suspend fun getCafeByCoords(lat: Float, long : Float): CafeBarRemoteResult = source.getBarCafe()
}