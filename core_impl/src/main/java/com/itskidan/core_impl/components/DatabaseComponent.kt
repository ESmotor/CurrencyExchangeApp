package com.itskidan.core_impl.components

import com.itskidan.core_api.providers.AppProvider
import com.itskidan.core_api.providers.DatabaseProvider
import com.itskidan.core_impl.modules.DatabaseModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [DatabaseModule::class]
)
interface DatabaseComponent : DatabaseProvider