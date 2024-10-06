package com.itskidan.currencyexchangeapp

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.ui.navigation.NavGraph
import com.itskidan.currencyexchangeapp.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var interactor: Interactor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        App.instance.dagger.inject(this)
        App.instance.screenWidthInDp = getScreenWidthInDp(this)
        App.instance.screenHeightInDp = getScreenHeightInDp(this)
        Timber.tag("MyLog").d("ScreenSize: (W:${App.instance.screenWidthInDp},H:${App.instance.screenHeightInDp})")
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            //observer in the activity
            lifecycle.addObserver(App.instance.lifecycleObserver)
            val navController = rememberNavController()
            AppTheme {
                NavGraph(navController)
            }
        }
    }


    private fun getScreenWidthInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val result = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return result
    }

    private fun getScreenHeightInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val result = (displayMetrics.heightPixels / displayMetrics.density).toInt()
        return result
    }


}


