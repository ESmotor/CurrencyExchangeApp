package com.itskidan.core_impl

import android.content.Context
import com.itskidan.core_api.ResourceManager
import com.itskidan.core_api.ResourceManagerProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ResourceManagerModule {

    @Provides
    @Singleton
    fun provideResourceManager(context: Context): ResourceManager {
        return AppResourceManager(context)
    }

    @Provides
    @Singleton
    fun provideResourceManagerProvider(resourceManager: ResourceManager): ResourceManagerProvider {
        return object : ResourceManagerProvider {
            override fun provideResourceManager(): ResourceManager {
                return resourceManager
            }
        }
    }
}