package com.angel.barcatcher.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontSynthesis.Companion.Style
import com.mapbox.maps.MapboxMap
import org.maplibre.android.camera.CameraPosition

@Composable
fun MapBoxView(
){
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(2.0)
                center(Point.fromLngLat(39.4716679, -6.4113187))
                pitch(0.0)
                bearing(0.0)
            }
}