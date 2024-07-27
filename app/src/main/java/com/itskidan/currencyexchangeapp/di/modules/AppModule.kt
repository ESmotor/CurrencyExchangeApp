package com.itskidan.currencyexchangeapp.di.modules

import android.content.SharedPreferences
import com.itskidan.currencyexchangeapp.application.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return App.instance.sharedPreferences
    }
}