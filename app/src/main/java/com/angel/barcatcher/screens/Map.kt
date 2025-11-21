package com.angel.barcatcher.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.angel.barcatcher.R
import com.angel.barcatcher.api.Model.Bar
import com.angel.barcatcher.api.Model.Cafebar
import com.angel.barcatcher.api.Model.Drinkbar
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
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.layers.generated.HeatmapLayer
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import retrofit2.Response

var vannotVisibility by mutableStateOf(false)
var vannotBar: Bar? by mutableStateOf(null)

@OptIn(MapboxDelicateApi::class, DelicateCoroutinesApi::class)
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

    var selectedBar by remember { mutableStateOf<Bar?>(null) }
    var showAnnotation by remember { mutableStateOf(false) }

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

                if (showAnnotation && selectedBar != null) {
                    when (selectedBar) {
                        is Bar.Cafe -> {
                            val cafeBar = (selectedBar as Bar.Cafe).data
                            cafeBar.location?.let { loc ->
                                if (loc.longitude != null && loc.latitude != null) {
                                    ViewAnnotation(
                                        options = viewAnnotationOptions {
                                            geometry(Point.fromLngLat(loc.longitude, loc.latitude))
                                            annotationAnchor {
                                                anchor(ViewAnnotationAnchor.TOP)
                                                offsetY(20.0)
                                            }
                                            allowOverlap(true)
                                            allowOverlapWithPuck(true)
                                            visible(true)
                                        }
                                    ) {
                                        MiniInfoCard(
                                            bar = cafeBar,
                                            navController = navController,
                                            onDismiss = {

                                                showAnnotation = false
                                            }
                                        )
                                    }
                                } else {
                                    Log.wtf("VIEWANNOTATION", "Location coords are null")
                                }
                            } ?: Log.wtf("VIEWANNOTATION", "Location is null")
                        }

                        is Bar.Drink -> {
                            val drinkBar = (selectedBar as Bar.Drink).data
                            drinkBar.location?.let { loc ->
                                if (loc.longitude != null && loc.latitude != null) {
                                    ViewAnnotation(
                                        options = viewAnnotationOptions {
                                            geometry(Point.fromLngLat(loc.longitude, loc.latitude))
                                            annotationAnchor {
                                                anchor(ViewAnnotationAnchor.TOP)
                                                offsetY(20.0)
                                            }
                                            allowOverlap(true)
                                            allowOverlapWithPuck(true)
                                            visible(true)
                                        }
                                    ) {
                                        MiniInfoCard(
                                            bar = drinkBar,
                                            navController = navController,
                                            onDismiss = {

                                                showAnnotation = false
                                            }
                                        )
                                    }
                                } else {
                                    Log.wtf("VIEWANNOTATION", "Location coords are null")
                                }
                            } ?: Log.wtf("VIEWANNOTATION", "Location is null")
                        }

                        else -> Log.wtf("VIEWANNOTATION", "selectedBar is neither Cafe nor Drink")
                    }
                } else {
                    Log.wtf(
                        "VIEWANNOTATION",
                        "Not showing: showAnnotation=$showAnnotation, selectedBar=${selectedBar != null}"
                    )
                }

                PrintPoints(
                    scannedBars = scannedBars,
                    onBarSelected = { bar ->
                        selectedBar = bar
                        showAnnotation = true
                    }
                )
            }
            if (hasLocationPermission.value) {
                Column(Modifier.align(Alignment.BottomEnd)) {
                    FloatingActionButton(
                        onClick = {
                            mapViewportState.transitionToFollowPuckState()
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                    }
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
fun PrintPoints(
    scannedBars: List<Bar>,
    onBarSelected: (Bar) -> Unit
) {
    val cafeIcon = rememberIconImage(
        key = "cafeIcon",
        painter = painterResource(R.drawable.baseline_sports_bar_24)
    )
    val drinkIcon = rememberIconImage(
        key = "drinkIcon",
        painter = painterResource(R.drawable.outline_local_bar_24)
    )

    if (scannedBars.isNotEmpty()) {
        scannedBars.forEach { bar ->
            when (bar) {
                is Bar.Cafe -> {
                    if (bar.data.location?.longitude != null && bar.data.location.latitude != null) {
                        Log.wtf("Printed ID", bar.data.toString())
                        PointAnnotation(
                            point = Point.fromLngLat(
                                bar.data.location.longitude,
                                bar.data.location.latitude
                            )
                        ) {
                            iconImage = cafeIcon
                            interactionsState.onClicked {
                                onBarSelected(bar)
                                true
                            }
                        }
                    }
                }

                is Bar.Drink -> {
                    if (bar.data.location?.longitude != null && bar.data.location.latitude != null) {
                        Log.wtf("Printed ID", bar.data.toString())
                        PointAnnotation(
                            point = Point.fromLngLat(
                                bar.data.location.longitude,
                                bar.data.location.latitude
                            )
                        ) {
                            iconImage = drinkIcon
                            interactionsState.onClicked {
                                onBarSelected(bar)
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
                if (it.data.location != null) {
                    if (it.data.location.longitude != null && it.data.location.latitude != null) {
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

            is Bar.Drink -> {
                if (it.data.location != null) {
                    if (it.data.location.longitude != null && it.data.location.latitude != null) {
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
    }
    return listFeatures
}

suspend fun scanBars(
    userPos: Location,
    cafeRepository: barCafeRepository,
    drinkRepository: barDrinkRepository
): List<Bar> {

    return coroutineScope {

        val cafesDeferred = async(Dispatchers.IO) {
            cafeRepository.getCafeByCoords(userPos.latitude, userPos.longitude)
        }

        val drinksDeferred = async(Dispatchers.IO) {
            drinkRepository.getDrinkByCoords(userPos.latitude, userPos.longitude)
        }

        val cafes = cafesDeferred.await().body()?.map { Bar.Cafe(it) } ?: emptyList()
        val drinks =
            drinksDeferred.await().body()?.map { Bar.Drink(it) } ?: emptyList()
        cafes + drinks
    }
}

suspend fun recoverAllBars(
    cafeRepository: barCafeRepository,
    drinkRepository: barDrinkRepository
): List<Bar> {

    return coroutineScope {
        val cafesDeferred = async { cafeRepository.getAllCafe() }
        val drinksDeferred = async { drinkRepository.getAllDrink() }
        val cafes = cafesDeferred.await().body()?.map { Bar.Cafe(it) } ?: emptyList()
        val drinks =
            drinksDeferred.await().body()?.map { Bar.Drink(it) } ?: emptyList()
        cafes + drinks
    }
}

@Composable
fun MiniInfoCard(
    bar: Cafebar,
    navController: NavController,
    onDismiss: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = bar.name,
                    modifier = Modifier.size(80.dp)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                bar.name.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_dialog_map),
                        contentDescription = "Dirección",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${bar.address.street}, ${bar.address.locality}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón para ver más detalles
                FilledTonalButton(
                    onClick = {
                        Log.wtf("CLICK DETAILS", "VER MAS CLICKED")
                        val id = bar.metadata.id

                        if (!id.isNullOrBlank() && id.contains("/")) {
                            val parts = id.split("/", limit = 2)
                            val type = parts[0]
                            val barID = parts[1]
                            navController.navigate("${AppScreens.BarInfo.route}/$type/$barID")
                        } else {
                            Log.e("BAR_ID", "El ID está vacío o no contiene '/' → id=$id")
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver más")
                }
            }
        }
    }
}

@Composable
fun MiniInfoCard(
    bar: Drinkbar,
    navController: NavController,
    onDismiss: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("✕", fontSize = 20.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = bar.name,
                    modifier = Modifier.size(80.dp)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = bar.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_dialog_map),
                        contentDescription = "Dirección",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${bar.address.street}, ${bar.address.locality}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = {
                        Log.wtf("CLICK DETAILS", bar.toString())
                        val id = bar.metadata.id

                        if (!id.isNullOrBlank() && id.contains("/")) {
                            val parts = id.split("/", limit = 2)
                            val type = parts[0]
                            val barID = parts[1]
                            navController.navigate("${AppScreens.BarInfo.route}/$type/$barID")
                        } else {
                            Log.e("BAR_ID", "El ID está vacío o no contiene '/' → id=$id")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver más")
                }
            }
        }
    }
}

