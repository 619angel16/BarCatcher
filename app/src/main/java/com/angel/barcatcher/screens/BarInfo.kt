package com.angel.barcatcher.screens

import android.util.Log
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
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angel.barcatcher.R
import com.angel.barcatcher.api.Model.Cafebar
import com.angel.barcatcher.api.Model.Drinkbar
import com.angel.barcatcher.navigation.AppScreens
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun BarInfo(
    navController: NavController,
    id: String,
    cafeBarRep: barCafeRepository,
    drinkBarRep: barDrinkRepository
) {
    Log.wtf("ID recuperado", id)
    if (id.contains("Cafebar", true)) {
        var bar by remember { mutableStateOf<List<Cafebar>?>(null) }
        LaunchedEffect(id) {
            val query = GlobalScope.async(Dispatchers.IO) { cafeBarRep.getCafe(id) }
            Log.wtf("Query result", query.await().body()!!.Results.toString())
            bar = query.await().body()?.Results
        }
        if (bar != null) {
            InfoCard(bar!!.first(), navController)
        }
    } else if (id.contains("Drinkbar", true)) {
        var bar by remember { mutableStateOf<List<Drinkbar>?>(null) }
        LaunchedEffect(id) {
            val query = GlobalScope.async(Dispatchers.IO) { drinkBarRep.getDrink(id) }
            Log.wtf("Query result", query.await().body()!!.Results.toString())
            bar = query.await().body()?.Results
        }
        if (bar != null) {
            InfoCard(bar!!.first(), navController)
        }
    }
}

@Composable
fun InfoCard(
    bar: Cafebar,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Imagen del bar cafe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = bar.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Contenido de la tarjeta
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Nombre del bar cafe
                    Text(
                        text = bar.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dirección
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_dialog_map),
                            contentDescription = "Dirección",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = bar.address.street + ", " + bar.address.locality + ", " + bar.address.country,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Teléfono (si está disponible)
                    if (bar.phone?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_call),
                                contentDescription = "Teléfono",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = bar.phone,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Descripción
                    //Inicio bloque de información
                    if (bar.capacity?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_group_24),
                                contentDescription = "Capacidad",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = bar.capacity,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                //Fin bloque de información

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    //Botón compartir
                    TextButton(onClick = { /* Compartir */ }) {
                        Text("Compartir")
                    }
                    //Botón Ver JSON
                    FilledTonalButton(onClick = {
                        val parts = bar.metadata.id.split("/", limit = 2)
                        if (parts.size == 2) {
                            val type = parts[0]
                            val barID = parts[1]
                            navController.navigate("${AppScreens.JSONViewer.route}/$type/$barID")
                        }
                    }) {
                        Text("Ver JSON")
                    }
                }
            }
        }
    }
}


@Composable
fun InfoCard(
    bar: Drinkbar, navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Imagen del bar de copas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = bar.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Contenido de la tarjeta
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Nombre del bar de copas
                    Text(
                        text = bar.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dirección
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_dialog_map),
                            contentDescription = "Dirección",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = bar.address.street + ", " + bar.address.locality + ", " + bar.address.country,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Teléfono (si está disponible)
                    if (bar.phone?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_call),
                                contentDescription = "Teléfono",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            bar.phone.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descripción
                    if (bar.capacity?.isNotEmpty() == true) {
                        Text(
                            text = bar.capacity,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { /* Compartir */ }) {
                            Text("Compartir")
                        }

                        FilledTonalButton(onClick = {
                            val parts = bar.metadata.id.split("/", limit = 2)
                            if (parts.size == 2) {
                                val type = parts[0]
                                val barID = parts[1]
                                navController.navigate("${AppScreens.JSONViewer.route}/$type/$barID")
                            }
                        }) {
                            Text("Ver JSON")
                        }
                    }
                }
            }
        }
    }
}