package com.itskidan.currencyexchangeapp.di

import android.app.Application
import android.content.Context
import com.itskidan.core_api.AppProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppContextComponent : AppProvider {
    companion object {
        private var appContextComponent: AppProvider? = null
        fun create(application: Application): AppProvider {
            return appContextComponent ?: DaggerAppContextComponent
                .builder()
                .application(application.applicationContext)
                .build().also {
                    appContextComponent = it
                }
        }
    }

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(context: Context): Builder
        fun build(): AppContextComponent
    }
}