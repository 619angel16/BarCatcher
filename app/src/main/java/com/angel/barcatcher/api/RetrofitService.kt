package com.angel.barcatcher.api

import android.content.Context
import android.util.Log
import com.angel.barcatcher.R
import com.angel.barcatcher.api.Model.CafeBarRemoteList
import com.angel.barcatcher.api.Model.CafeBarRemoteResult
import com.angel.barcatcher.api.Model.DrinkBarRemoteList
import com.angel.barcatcher.api.Model.DrinkBarRemoteResult
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

interface RetrofitService {


    @GET("docs")
    suspend fun getBarCafe(@Query("id") id: String): Response<CafeBarRemoteResult>

    @GET("streams/queries?query=from+\"Cafebar\"")
    suspend fun getAllBarCafe(): Response<CafeBarRemoteList>

    @GET("docs")
    suspend fun getBarDrink(@Query("id") id: String): Response<DrinkBarRemoteResult>

    @GET("streams/queries?query=from+\"Drinkbar\"")
    suspend fun getAllBarDrink(): Response<DrinkBarRemoteList>

    //TODO impl get by coords

    object RetrofitServiceFactory {
        private fun generateSecureOkHttpClient(context: Context): OkHttpClient {
            val httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)

            try {
                // Cargar el KeyStore desde el PFX
                val keyStore = KeyStore.getInstance("PKCS12")
                val pfxInputStream =
                    context.resources.openRawResource(R.raw.apk)
                keyStore.load(
                    pfxInputStream,
                    "apk1234".toCharArray()
                )

                // Crear KeyManager con el certificado cliente
                val keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                keyManagerFactory.init(keyStore, "apk1234".toCharArray())

                // Crear TrustManager confiando en CA del sistema (opcional: también podrías cargar un trust personalizado)
                val trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(null as KeyStore?) // null = confía en sistema operativo

                // Crear SSLContext con ambos
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(
                    keyManagerFactory.keyManagers,
                    trustManagerFactory.trustManagers,
                    null
                )

                val trustManager = trustManagerFactory.trustManagers
                    .first { it is X509TrustManager } as X509TrustManager

                return httpClientBuilder
                    .sslSocketFactory(sslContext.socketFactory, trustManager)
                    .build()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("RetrofitServiceFactory", "Error SSL: ${e.message}")
                return httpClientBuilder.build()
            }
        }

        fun makeRetrofitService(context: Context): RetrofitService {
            return Retrofit.Builder()
                .baseUrl("https://a.free.apeaorre.ravendb.cloud/databases/PIM_Testing/")
                .client(generateSecureOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService::class.java)
        }
    }
}