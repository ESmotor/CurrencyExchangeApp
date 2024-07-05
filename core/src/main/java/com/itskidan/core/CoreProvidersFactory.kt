package com.itskidan.core

import com.itskidan.core_api.AppProvider
import com.itskidan.core_api.DatabaseProvider
import com.itskidan.core_impl.DaggerDatabaseComponent

object CoreProvidersFactory {
    fun createDatabaseBuilder(appProvider: AppProvider): DatabaseProvider {
        return DaggerDatabaseComponent.builder().appProvider(appProvider).build()
    }
}