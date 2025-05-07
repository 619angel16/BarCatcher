package com.angel.barcatcher

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.angel.barcatcher.api.RetrofitService
import com.angel.barcatcher.navigation.AppNavigation
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = RetrofitService.RetrofitServiceFactory.makeRetrofitService(this)
        val drinkBar = barDrinkRepository(service)
        val cafeBar = barCafeRepository(service)
        Log.wtf("Retrofit Created!!", "Retrofit has been created!")
        setContent { AppNavigation(this, service, drinkBar, cafeBar) }
    }
}

/*@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, service: RetrofitService) {
    val cafebars = getTest(service)?.body()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(255, 0, 255, 100))
    ) {
        if (cafebars != null) {
            if (cafebars.Results == null) {
                Text(
                    text = "Hello $name!",
                    modifier = modifier
                )
                Text(text = "Se ha cometido un error")
                Log.wtf("Error Consulta", "No se ha recuperado la info")
                Log.wtf("Error PostConsultaDatos", cafebars.toString())
            } else {
                for (cafebar in cafebars.Results) {
                    Log.wtf("Consulta exitosa", cafebar.toString())
                    Text(
                        text = "Country: ${cafebar}",
                        modifier = modifier
                    )
                }
            }
        }
    }
}*/

/*
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun getTest(service: RetrofitService): Response<CafeBarRemoteList>? {
    var result by remember { mutableStateOf<Response<CafeBarRemoteList>?>(null) }
    LaunchedEffect(true) {
        val query = GlobalScope.async(Dispatchers.IO) { service.getAllBarCafe() }
        result = query.await()
        Log.wtf("Info consulta?", result!!.body().toString())
    }
    return result
}
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun getTest2(service: RetrofitService): Response<CafeBarRemoteResult>? {
    var result by remember { mutableStateOf<Response<CafeBarRemoteResult>?>(null) }
    LaunchedEffect(true) {
        val query = GlobalScope.async(Dispatchers.IO) { service.getBarCafe("Cafebar/0000000000000003816-A") }
        result = query.await()
        Log.wtf("Info consulta?", result!!.body().toString())
    }
    return result
}*/
