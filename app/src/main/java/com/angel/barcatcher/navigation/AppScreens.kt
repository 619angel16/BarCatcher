package com.angel.barcatcher.navigation

sealed class AppScreens(val route: String){

    object BarList: AppScreens("BarList")

    object BarInfo: AppScreens("BarInfo")

    object JSONViewer: AppScreens("JSONViewer")

}
