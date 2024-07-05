package com.itskidan.currencyexchangeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.itskidan.currencyexchangeapp.ui.addcurrency.AddCurrencyScreen
import com.itskidan.currencyexchangeapp.utils.Constants
import com.itskidan.currencyexchangeapp.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Constants.HOME
    ) {
        composable(Constants.HOME) { HomeScreen(navController) }
        composable(Constants.ADD_CURRENCY) {
            AddCurrencyScreen(navController = navController)
        }
//        composable("profile/{userId}", arguments = listOf(navArgument("userId") { /* ... */ })) { /* Screen Profile */ }
    }
}