package com.angel.barcatcher.api.DAO

import com.angel.barcatcher.Model.Bar
import retrofit2.http.HTTP

interface BarDAO {

    @HTTP("FROM Cafebar WHERE @id = :barID")
    suspend fun getAnimalById(barID: String): Bar
}