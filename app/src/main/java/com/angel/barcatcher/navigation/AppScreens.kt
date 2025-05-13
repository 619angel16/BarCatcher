package com.angel.barcatcher.navigation

sealed class AppScreens(val route: String){

    object BarInfo: AppScreens("BarInfo")

    object JSONViewer: AppScreens("JSONViewer")

    object Map: AppScreens("Map")

}
