package com.itskidan.currencyexchangeapp.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.itskidan.core.CoreProvidersFactory
import com.itskidan.core_impl.DaggerResourceManagerComponent
import com.itskidan.currencyexchangeapp.BuildConfig
import com.itskidan.currencyexchangeapp.di.AppComponent
import com.itskidan.currencyexchangeapp.di.AppContextComponent
import com.itskidan.currencyexchangeapp.di.DaggerAppComponent
import com.itskidan.currencyexchangeapp.di.modules.DomainModule
import com.itskidan.currencyexchangeapp.lifecycle.LifecycleObserver
import com.itskidan.remote_module.DaggerRemoteComponent
import timber.log.Timber

class App : Application() {
    lateinit var sharedPreferences: SharedPreferences
    val lifecycleObserver = LifecycleObserver()
    var version = "none"
    lateinit var dagger: AppComponent
    var screenWidthInDp = 0
    var screenHeightInDp = 0

    override fun onCreate() {
        super.onCreate()
        instance = this
        val remoteProvider = DaggerRemoteComponent.create()
        val appProvider = AppContextComponent.create(this)
        val resourceManagerProvider = DaggerResourceManagerComponent.factory().create(this)
        val databaseProvider = CoreProvidersFactory.createDatabaseBuilder(appProvider)
        dagger = DaggerAppComponent.builder()
            .remoteProvider(remoteProvider)
            .appProvider(appProvider)
            .resourceManagerProvider(resourceManagerProvider)
            .domainModule(DomainModule())
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