package com.angel.barcatcher.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angel.barcatcher.api.Model.CafeBarRemoteList
import com.angel.barcatcher.api.Model.Cafebar
import com.angel.barcatcher.api.Model.DrinkBarRemoteList
import com.angel.barcatcher.api.Model.Drinkbar
import com.angel.barcatcher.navigation.AppScreens
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@Composable
fun BarListActivity(
    navController: NavController, drinkBarRep: barDrinkRepository,
    cafeBarRep: barCafeRepository
) {
    var cafebars by remember { mutableStateOf<CafeBarRemoteList?>(null) }
    LaunchedEffect(true) {
        val query = GlobalScope.async(Dispatchers.IO) { cafeBarRep.getAllCafe() }
        cafebars = query.await().body()
    }
    var drinkbars by remember { mutableStateOf<DrinkBarRemoteList?>(null) }
    LaunchedEffect(true) {
        val query = GlobalScope.async(Dispatchers.IO) { drinkBarRep.getAllDrink() }
        drinkbars = query.await().body()
    }
    drinkbars?.let {
        cafebars?.let { it1 ->
            TwoColumnList(
                it1.Results,
                it.Results,
                navController
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TwoColumnList(
    cafeList: List<Cafebar>,
    drinkList: List<Drinkbar>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.labelLarge,
                            text = "Bares Cafetería"
                        )
                    }
                }
                items(cafeList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable {
                                val parts = item.metadata.id.split("/", limit = 2)
                                if (parts.size == 2) {
                                    val type = parts[0]
                                    val barID = parts[1]
                                    navController.navigate("${AppScreens.BarInfo.route}/$type/$barID")
                                }
                            },
                        colors = cardColors(MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.labelLarge,
                            text = "Bares de copas"
                        )
                    }
                }
                items(drinkList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .clickable {
                                val parts = item.metadata.id.split("/", limit = 2)
                                if (parts.size == 2) {
                                    val type = parts[0]
                                    val barID = parts[1]
                                    navController.navigate("${AppScreens.BarInfo.route}/$type/$barID")
                                }
                            },
                        colors = cardColors(MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = item.name,
                            modifier = Modifier
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}