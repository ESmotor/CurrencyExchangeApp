package com.itskidan.currencyexchangeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.ui.navigation.NavGraph
import com.itskidan.currencyexchangeapp.ui.theme.AppTheme
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var interactor: Interactor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        App.instance.dagger.inject(this)

//        updateDataBase()

        setContent {
            //observer in the activity
            lifecycle.addObserver(App.instance.lifecycleObserver)
            val navController = rememberNavController()
            AppTheme {
                NavGraph(navController)
            }
        }
    }

    private fun updateDataBase() {
        lifecycleScope.launch {
            CurrencyUtils.currencyFlagMap.map { (key, value) ->
                val currencyName = when (key) {
                    "USD" -> "United State Dollar"
                    "RUB" -> "Russian Ruble"
                    "TRY" -> "Turkish Lira"
                    "CNY" -> "Chinese Yuan"
                    "CHF" -> "Swiss Franc"
                    "KZT" -> "Kazakhstan Tenge"
                    "EUR" -> "European Union Euro"
                    else -> "unknown currency"
                }
                val currencyBidValue = (10000..20000).random() / 100.0
                val currencyAskValue = currencyBidValue + (0..200).random() / 100.0

                interactor.putCurrencyToDB(
                    Currency(
                        currencyCode = key,
                        currencyName = currencyName,
                        currencyFlagId = value,
                        currencyAskValue = currencyAskValue,
                        currencyBidValue = currencyBidValue
                    )
                )
            }
        }
    }
}


