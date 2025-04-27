package com.angel.barcatcher

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.angel.barcatcher.api.Model.RemoteResult
import com.angel.barcatcher.api.RetrofitService
import com.angel.barcatcher.ui.theme.BarCatcherTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = RetrofitService.RetrofitServiceFactory.makeRetrofitService(this)
        Log.wtf("Retrofit Created!!", "Retrofit has been created!")

        /*val insert = CafeBar(
            name = "Casa Paco",
            tel = "123546",
            streetAddress = "Av. Virgen de la Montaña, nº 26",
            addressLocality = "Cáceres",
            postalCode = 10002,
            addressCountry = "ES",
            geolong = null,
            url = null,
            geolat = null,
            capacity = null,
            email = null,
            customId = "Cafebar/619"
        )*/
        enableEdgeToEdge()
        setContent {
            BarCatcherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        service
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, service: RetrofitService) {
    val test = getTest(service)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(255, 0, 255, 100))
    ) {
        if (test == null) {
            Text(
                text = "Hello $name!",
                modifier = modifier
            )
            Log.wtf("Error Consulta", "No se ha recuperado la info")
        } else {
            val countries = getTest(service)?.body()?.Results
            if (countries != null) {
                for (country in countries) {
                    Log.wtf("Consulta exitosa", country.name)
                    Text(
                        text = "Country: ${country.name}",
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun getTest(service: RetrofitService): Response<RemoteResult>? {
    var result by remember { mutableStateOf<Response<RemoteResult>?>(null) }
    LaunchedEffect(true) {
        val query = GlobalScope.async(Dispatchers.IO) { service.getDoc() }
        result = query.await()
        Log.wtf("Info consulta?", result!!.toString())
    }
    return result
}