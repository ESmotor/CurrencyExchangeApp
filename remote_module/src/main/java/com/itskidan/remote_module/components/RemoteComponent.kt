package com.itskidan.remote_module.components

import com.itskidan.remote_module.modules.RemoteModule
import com.itskidan.remote_module.providers.RemoteProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RemoteModule::class]
)
interface RemoteComponent : RemoteProvider