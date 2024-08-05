package com.itskidan.core_impl

import android.content.Context
import com.itskidan.core_api.ResourceManagerProvider
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