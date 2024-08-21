package com.itskidan.core_impl.components

import android.content.Context
import com.itskidan.core_api.providers.ResourceManagerProvider
import com.itskidan.core_impl.modules.ResourceManagerModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ResourceManagerModule::class])
interface ResourceManagerComponent : ResourceManagerProvider {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ResourceManagerComponent
    }
}