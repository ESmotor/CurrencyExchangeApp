package com.itskidan.core_impl

import com.itskidan.core_api.AppProvider
import com.itskidan.core_api.DatabaseProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [AppProvider::class],
    modules = [DatabaseModule::class]
)
interface DatabaseComponent : DatabaseProvider