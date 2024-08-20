package com.itskidan.core_api.providers

import com.itskidan.core_api.ResourceManager

interface ResourceManagerProvider {
    fun provideResourceManager(): ResourceManager
}