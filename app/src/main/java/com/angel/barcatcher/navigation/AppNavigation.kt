package com.angel.barcatcher.navigation

import BarJsonViewer
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.angel.barcatcher.api.RetrofitService
import com.angel.barcatcher.repository.barCafeRepository
import com.angel.barcatcher.repository.barDrinkRepository
import com.angel.barcatcher.screens.BarInfo
import com.angel.barcatcher.screens.BarListActivity


/*Elemento composable que se va a encargar de orquestar la navegacion, va a conocer
las pantallas de nuestra app y se va a encargar de gestionar el paso entre ellas*/
@Composable
fun AppNavigation(
    context: Context,
    service: RetrofitService,
    drinkBar: barDrinkRepository,
    cafeBar: barCafeRepository
) {

    val navController = rememberNavController()

    //El elemento NavHost va a conocer las pantallas y como navegar entre ellas
    NavHost(navController = navController, startDestination = AppScreens.BarList.route) {
        //El navHost estara formado por diferente composables que seran cada una de nuestras pantallas
        composable(route = AppScreens.BarList.route) {
            BarListActivity(navController, drinkBar, cafeBar)
        }

        composable(
            route = AppScreens.BarInfo.route + "/{type}/{barID}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("barID") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val barID = backStackEntry.arguments?.getString("barID") ?: ""
            val fullId = "$type/$barID"

            BarInfo(
                navController,
                fullId,
                cafeBar,
                drinkBar
            )
        }
        composable(
            route = AppScreens.JSONViewer.route + "/{type}/{barID}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("barID") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val barID = backStackEntry.arguments?.getString("barID") ?: ""
            val fullId = "$type/$barID"

            if (type.contains("Cafebar")) {
                BarJsonViewer(
                    fullId,
                    cafeBar
                )
            }else if (type.contains("Drinkbar")){
                BarJsonViewer(
                    fullId,
                    drinkBar
                )
            }
        }
    }
}