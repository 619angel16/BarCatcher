package com.angel.barcatcher.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.angel.barcatcher.R
import com.angel.barcatcher.api.Model.Bar
import com.angel.barcatcher.navigation.AppScreens
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxDelicateApi
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.layers.generated.HeatmapLayer
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(MapboxDelicateApi::class)
@Composable
fun MapBoxView(
    context: Context,
    cafeRepository: barCafeRepository,
    drinkRepository: barDrinkRepository,
    navController: NavController
) {
    val locationService: LocationService = LocationServiceFactory.getOrCreate()
    var locationProvider: DeviceLocationProvider? = null
    var location by remember { mutableStateOf<Location?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val geoJsonSource = rememberGeoJsonSourceState {
        generateId = BooleanValue(true)
    }
    var heatMode by remember { mutableStateOf(true) }
    var scannedBars by remember { mutableStateOf<List<Bar>>(emptyList()) }

    val request = LocationProviderRequest.Builder()
        .interval(
            IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build()
        )
        .displacement(0F)
        .accuracy(AccuracyLevel.HIGHEST)
        .build()

    val result = locationService.getDeviceLocationProvider(request)
    if (result.isValue) {
        locationProvider = result.value!!
    } else {
        Log.e("Location ERROR", "Failed to get device location provider")
    }
    val hasLocationPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission.value = isGranted
    }

    // Solicitar permisos al iniciar
    LaunchedEffect(key1 = Unit) {
        if (!hasLocationPermission.value) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    center(Point.fromLngLat(-6.37224, 39.47649))
                    zoom(12.0)
                    pitch(0.0)
                    bearing(0.0)
                }
            }

            // Mapbox MapView con Compose
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState
            ) {
                MapEffect(Unit) { mapView ->

                    mapView.location.updateSettings {

                        locationPuck = createDefault2DPuck(withBearing = true)

                        enabled = true

                        puckBearing = PuckBearing.COURSE

                        puckBearingEnabled = true

                    }
                }
                if (!heatMode) {
                    HeatmapLayer(layerId = "heatMapLayer", sourceState = geoJsonSource)
                }

                PrintPoints(scannedBars, navController)
            }
            if (hasLocationPermission.value) {
                Column(Modifier.align(Alignment.BottomEnd)) {
                    // Botón para ir a la ubicación actual
                    FloatingActionButton(
                        onClick = {
                            mapViewportState.transitionToFollowPuckState()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                    }
                    //Boton para escanear cercanías
                    FloatingActionButton(
                        onClick = {
                            locationProvider?.getLastLocation { result ->
                                result?.let { loc ->
                                    location = loc
                                    Log.wtf(
                                        "Recovered location",
                                        "Location: ${loc.latitude}, ${loc.longitude}"
                                    )
                                    coroutineScope.launch {
                                        val bars = location?.let {
                                            scanBars(
                                                it,
                                                cafeRepository,
                                                drinkRepository
                                            )
                                        }
                                        if (bars != null) {
                                            scannedBars = bars
                                        }
                                    }
                                }
                            } ?: Log.wtf("Location", "No location available")

                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_radar_24),
                            contentDescription = "Scan"
                        )
                    }

                }
                Column(Modifier.align(Alignment.BottomStart)) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val bars = recoverAllBars(
                                    cafeRepository,
                                    drinkRepository
                                )
                                Log.wtf("BARS CHECK", bars.toString())
                                Log.wtf("DATA HEATMAP?", getBarFeatures(bars).toString())
                                geoJsonSource.data = GeoJSONData(getBarFeatures(bars))
                                heatMode = !heatMode
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_mode_heat_24),
                            contentDescription = "Heat mode"
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun PrintPoints(scannedBars: List<Bar>, navController: NavController) {
    val cafeIcon = rememberIconImage(
        key = "cafeIcon",
        painter = painterResource(R.drawable.baseline_sports_bar_24)
    )
    val drinkIcon = rememberIconImage(
        key = "drinkIcon",
        painter = painterResource(R.drawable.outline_local_bar_24)
    )
    if (scannedBars.isNotEmpty()) {
        scannedBars.forEach {
            when (it) {
                is Bar.Cafe -> {
                    if (it.data.location?.longitude != null && it.data.location.latitude != null) {
                        val bar = it
                        PointAnnotation(
                            point = Point.fromLngLat(
                                it.data.location.longitude,
                                it.data.location.latitude
                            )
                        ) {
                            iconImage = cafeIcon
                            interactionsState.onClicked {
                                val parts =
                                    bar.data.metadata.id.split("/", limit = 2)
                                if (parts.size == 2) {
                                    val type = parts[0]
                                    val barID = parts[1]
                                    navController.navigate("${AppScreens.BarInfo.route}/$type/$barID")
                                }
                                true
                            }
                        }

                    }
                }

                is Bar.Drink -> {
                    if (it.data.location?.longitude != null && it.data.location.latitude != null) {

                        val bar = it
                        PointAnnotation(
                            point = Point.fromLngLat(
                                it.data.location.longitude,
                                it.data.location.latitude
                            )
                        ) {
                            iconImage = drinkIcon
                            interactionsState.onClicked {
                                val parts =
                                    bar.data.metadata.id.split("/", limit = 2)
                                if (parts.size == 2) {
                                    val type = parts[0]
                                    val barID = parts[1]
                                    navController.navigate("${AppScreens.BarInfo.route}/$type/$barID")
                                }
                                true
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getBarFeatures(bars: List<Bar>): List<Feature> {
    Gson()
    var listFeatures: List<Feature> = emptyList()
    bars.forEach {
        when (it) {
            is Bar.Cafe -> {
                if (it.data.location?.longitude != null && it.data.location.latitude != null) {
                    val geometry =
                        Point.fromLngLat(it.data.location.longitude, it.data.location.latitude)
                    val properties = JsonObject().apply {
                        addProperty("locality", it.data.address.locality)
                        addProperty("country", it.data.address.country)
                        addProperty("postalCode", it.data.address.postalCode)
                    }
                    listFeatures = listFeatures + Feature.fromGeometry(geometry, properties)
                }
            }

            is Bar.Drink -> {
                if (it.data.location?.longitude != null && it.data.location.latitude != null) {
                    val geometry =
                        Point.fromLngLat(it.data.location.longitude, it.data.location.latitude)
                    val properties = JsonObject().apply {
                        addProperty("locality", it.data.address.locality)
                        addProperty("country", it.data.address.country)
                        addProperty("postalCode", it.data.address.postalCode)
                    }
                    listFeatures = listFeatures + Feature.fromGeometry(geometry, properties)
                }
            }
        }
    }
    return listFeatures
}

suspend fun scanBars(
    userPos: Location,
    cafeRepository: barCafeRepository,
    drinkRepository: barDrinkRepository
): List<Bar> {

    return kotlinx.coroutines.coroutineScope {

        val cafesDeferred = async(Dispatchers.IO) {
            cafeRepository.getCafeByCoords(userPos.latitude, userPos.longitude)
        }

        val drinksDeferred = async(Dispatchers.IO) {
            drinkRepository.getDrinkByCoords(userPos.latitude, userPos.longitude)
        }

        val cafes = cafesDeferred.await().body()?.Results?.map { Bar.Cafe(it) } ?: emptyList()
        val drinks =
            drinksDeferred.await().body()?.Results?.map { Bar.Drink(it) } ?: emptyList()
        cafes + drinks
    }
}

suspend fun recoverAllBars(
    cafeRepository: barCafeRepository,
    drinkRepository: barDrinkRepository
): List<Bar> {

    return kotlinx.coroutines.coroutineScope {

        val cafesDeferred = async(Dispatchers.IO) {
            cafeRepository.getAllCafe()
        }

        val drinksDeferred = async(Dispatchers.IO) {
            drinkRepository.getAllDrink()
        }

        val cafes = cafesDeferred.await().body()?.Results?.map { Bar.Cafe(it) } ?: emptyList()
        val drinks =
            drinksDeferred.await().body()?.Results?.map { Bar.Drink(it) } ?: emptyList()
        cafes + drinks
    }
}

