package com.angel.barcatcher

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.angel.barcatcher.api.RetrofitService
import com.angel.barcatcher.navigation.AppNavigation
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import org.maplibre.android.style.expressions.Expression.zoom
import org.maplibre.geojson.Point

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = RetrofitService.RetrofitServiceFactory.makeRetrofitService(this)
        val drinkBar = barDrinkRepository(service)
        val cafeBar = barCafeRepository(service)
        Log.wtf("Retrofit Created!!", "Retrofit has been created!")
        setContent { AppNavigation(this, drinkBar, cafeBar) }
    }
}
