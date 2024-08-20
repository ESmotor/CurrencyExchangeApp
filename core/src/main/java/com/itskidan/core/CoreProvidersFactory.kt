package com.itskidan.core

import com.itskidan.core_api.providers.AppProvider
import com.itskidan.core_api.providers.DatabaseProvider
import com.itskidan.core_impl.components.DaggerDatabaseComponent

object CoreProvidersFactory {
    fun createDatabaseBuilder(
        appProvider: AppProvider,
    ): DatabaseProvider {
        return DaggerDatabaseComponent.builder()
            .appProvider(appProvider)
            .build()
    }
}