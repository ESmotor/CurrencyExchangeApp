package com.itskidan.currencyexchangeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.itskidan.currencyexchangeapp.ui.addcurrency.AddCurrencyScreen
import com.itskidan.currencyexchangeapp.ui.changecurreny.ChangeCurrencyScreen
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
        composable("${Constants.CHANGE_CURRENCY}/{currencyCode}/{currencyValue}/{isFocused}") { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: "USD"
            val currencyValue = backStackEntry.arguments?.getString("currencyValue") ?: "1"
            val isFocusedString = backStackEntry.arguments?.getString("isFocused") ?: "true"
            val isFocused = isFocusedString.toBoolean()
            ChangeCurrencyScreen(
                navController = navController,
                oldCurrencyCode = currencyCode,
                oldCurrencyValue = currencyValue,
                isFocused = isFocused
            )
        }
    }
}