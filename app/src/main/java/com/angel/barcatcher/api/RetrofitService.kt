package com.angel.barcatcher.api
import android.content.Context
import com.angel.barcatcher.R
import com.angel.barcatcher.api.Model.RemoteResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

interface RetrofitService {


    @GET("animals/{id}")
    suspend fun getBar(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
    ) : RemoteResult

    object RetrofitServiceFactory {
        private fun generateSecureOkHttpClient(context: Context): OkHttpClient {
            // Create a simple builder for our http client, this is only por example purposes
            var httpClientBuilder = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)

            // Here you may wanna add some headers or custom setting for your builder

            // Get the file of our certificate
            var caFileInputStream = context.resources.openRawResource(R.raw.apk)

            // We're going to put our certificates in a Keystore
            val keyStore = KeyStore.getInstance("PKCS12")
            keyStore.load(caFileInputStream, "my file password".toCharArray())

            // Create a KeyManagerFactory with our specific algorithm our our public keys
            // Most of the cases is gonna be "X509"
            val keyManagerFactory = KeyManagerFactory.getInstance("X509")
            keyManagerFactory.init(keyStore, "my file password".toCharArray())

            // Create a SSL context with the key managers of the KeyManagerFactory
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(keyManagerFactory.keyManagers, null, SecureRandom())

            //Finally set the sslSocketFactory to our builder and build it
            return httpClientBuilder
                .sslSocketFactory(sslContext.socketFactory)
                .build()
        }
        fun makeRetrofitService(): RetrofitService {
            return Retrofit.Builder()
                .baseUrl("https://a.free.apeaorre.ravendb.cloud/databases/PIM_Testing/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RetrofitService::class.java)
        }
    }
}