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
        setContent { AppNavigation(drinkBar, cafeBar) }
    }
}
