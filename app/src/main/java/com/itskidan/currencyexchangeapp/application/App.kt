package com.itskidan.currencyexchangeapp.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.itskidan.core.CoreProvidersFactory
import com.itskidan.currencyexchangeapp.BuildConfig
import com.itskidan.currencyexchangeapp.di.AppComponent
import com.itskidan.currencyexchangeapp.di.AppContextComponent
import com.itskidan.currencyexchangeapp.di.DaggerAppComponent
import com.itskidan.currencyexchangeapp.di.modules.DomainModule
import com.itskidan.currencyexchangeapp.lifecycle.LifecycleObserver
import timber.log.Timber

class App : Application() {
    lateinit var sharedPreferences: SharedPreferences
    val lifecycleObserver = LifecycleObserver()
    var version = "none"
    lateinit var dagger: AppComponent


    override fun onCreate() {
        super.onCreate()
        instance = this
        val appProvider = AppContextComponent.create(this)
        val databaseProvider = CoreProvidersFactory.createDatabaseBuilder(appProvider)
        dagger = DaggerAppComponent.builder()
            .appProvider(appProvider)
            .domainModule(DomainModule(this))
            .databaseProvider(databaseProvider)
            .build()

        sharedPreferences =
            instance.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var instance: App
            private set
    }


}