package com.itskidan.currencyexchangeapp

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
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
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var interactor: Interactor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        App.instance.dagger.inject(this)
        App.instance.screenWidthInDp = getScreenWidthInDp(this)
        App.instance.screenHeightInDp = getScreenHeightInDp(this)
//        updateDataBase(this)

        setContent {
            //observer in the activity
            lifecycle.addObserver(App.instance.lifecycleObserver)
            val navController = rememberNavController()
            AppTheme {
                NavGraph(navController)
            }
        }
    }

    private fun updateDataBase(context: Context) {
        lifecycleScope.launch {
            var id = 0
            CurrencyUtils.currencyCodeList.map { code ->
                val currencyName = CurrencyUtils.createCurrencyNameMap(context)[code]?:"Unknown Currency"
                val currencyFlagId = CurrencyUtils.currencyFlagMap[code]?:0
                val currencyBidValue = (10000..20000).random() / 100.0
                val currencyAskValue = currencyBidValue + (0..200).random() / 100.0

                interactor.putCurrencyToDatabase(
                    Currency(
                        id = id++,
                        currencyCode = code,
                        currencyName = currencyName,
                        currencyFlagId = currencyFlagId,
                        currencyAskValue = currencyAskValue,
                        currencyBidValue = currencyBidValue
                    )
                )
            }
        }
    }
    fun getScreenWidthInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val result = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        Timber.tag("MyLog").d("width = %s", result)
        return result
    }

    fun getScreenHeightInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val result = (displayMetrics.heightPixels / displayMetrics.density).toInt()
        Timber.tag("MyLog").d("height = %s", result)
        return result
    }
}


