package com.itskidan.currencyexchangeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.itskidan.currencyexchangeapp.ui.screens.addcurrency.AddCurrencyScreen
import com.itskidan.currencyexchangeapp.ui.screens.calculator.CalculatorScreen
import com.itskidan.currencyexchangeapp.ui.screens.changecurreny.ChangeCurrencyScreen
import com.itskidan.currencyexchangeapp.utils.NavigationConstants
import com.itskidan.currencyexchangeapp.ui.screens.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationConstants.HOME
    ) {
        composable(NavigationConstants.HOME) { HomeScreen(navController) }

        composable(
            route = "${NavigationConstants.ADD_CURRENCY}/{locationOfRequest}",
            arguments = listOf(
                navArgument("locationOfRequest") { type = NavType.StringType },
            )
        ) {backStackEntry ->
            val locationOfRequest = backStackEntry.arguments?.getString("locationOfRequest") ?: "unknown_location"
            AddCurrencyScreen(
                navController = navController,
                locationOfRequest = locationOfRequest,
            )
        }

        composable(
            route = "${NavigationConstants.CHANGE_CURRENCY}/{currencyCode}/{currencyValue}/{isFocused}/{locationOfRequest}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyValue") { type = NavType.StringType },
                navArgument("isFocused") { type = NavType.BoolType },
                navArgument("locationOfRequest") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: "USD"
            val currencyValue = backStackEntry.arguments?.getString("currencyValue") ?: "1"
            val isFocused = backStackEntry.arguments?.getBoolean("isFocused") ?: true
            val locationOfRequest = backStackEntry.arguments?.getString("locationOfRequest") ?: "unknown_location"
            ChangeCurrencyScreen(
                navController = navController,
                oldCurrencyCode = currencyCode,
                oldCurrencyValue = currencyValue,
                isFocused = isFocused,
                locationOfRequest = locationOfRequest
            )
        }
        composable(
            route="${NavigationConstants.CALCULATOR}/{currencyCode}/{currencyValue}/{locationOfRequest}",
            arguments = listOf(
                navArgument("currencyCode") { type = NavType.StringType },
                navArgument("currencyValue") { type = NavType.StringType },
                navArgument("locationOfRequest") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: "USD"
            val currencyValue = backStackEntry.arguments?.getString("currencyValue") ?: "0"
            val locationOfRequest = backStackEntry.arguments?.getString("locationOfRequest") ?: "unknown_location"
            CalculatorScreen(
                navController = navController,
                currencyCode = currencyCode,
                currencyValue = currencyValue,
                locationOfRequest = locationOfRequest
            )
        }
    }
}