package com.angel.barcatcher.api

import android.content.Context
import com.angel.barcatcher.api.Model.CafeBarRemoteList
import com.angel.barcatcher.api.Model.CafeBarRemoteResult
import com.angel.barcatcher.api.Model.Cafebar
import com.angel.barcatcher.api.Model.DrinkBarRemoteList
import com.angel.barcatcher.api.Model.DrinkBarRemoteResult
import com.angel.barcatcher.api.Model.Drinkbar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface RetrofitService {


    @GET("cafebars/campo?nombre=name&valor=")
    suspend fun getBarCafe(@Path("id") id: String): Response<CafeBarRemoteResult>

    @GET("cafebars")
    suspend fun getAllBarCafe(): Response<CafeBarRemoteList>

    @GET("drinkbars/campo?nombre=name&valor=")
    suspend fun getBarDrink(@Path("id") id: String): Response<DrinkBarRemoteResult>

    @GET("drinkbars")
    suspend fun getAllBarDrink(): Response<DrinkBarRemoteList>

    @GET("cafebars/cercano?")
    suspend fun getCafeByCoords(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Float
    ): Response<List<Cafebar>>

    @GET("drinkbars/cercano?")
    suspend fun getDrinkByCoords(
        @Query("latitud") latitud: Double,
        @Query("longitud") longitud: Double,
        @Query("radio") radio: Float
    ): Response<List<Drinkbar>>

    object RetrofitServiceFactory {
        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        fun makeRetrofitService(context: Context): RetrofitService {
            return Retrofit.Builder()
                .baseUrl("http://192.168.1.133:5000/api/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService::class.java)
        }
    }
}