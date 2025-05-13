package com.angel.barcatcher.repository

import com.angel.barcatcher.api.Model.CafeBarRemoteList
import com.angel.barcatcher.api.Model.CafeBarRemoteResult
import com.angel.barcatcher.api.RetrofitService
import retrofit2.Response

class barCafeRepository(service: RetrofitService) {
    var source = service

    suspend fun getCafe(ID: String): Response<CafeBarRemoteResult> = source.getBarCafe(ID)

    suspend fun getAllCafe(): Response<CafeBarRemoteList> = source.getAllBarCafe()

    private fun buildQuery(lat: Double, long: Double): String {
        return "from \"Cafebars\" where spatial.within(spatial.point(latitude, longitude), spatial.circle(0.28, $lat, $long))"
    }

    suspend fun getCafeByCoords(lat: Double, long: Double): Response<CafeBarRemoteList> =
        source.getCafeByCoords(buildQuery(lat, long))


    //TODO IMPL suspend fun getCafeByCoords(lat: Float, long : Float): CafeBarRemoteResult = source.getBarCafe()
}