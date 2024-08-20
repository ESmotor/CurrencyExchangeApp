package com.itskidan.remote_module.providers

import com.itskidan.remote_module.api.CurrencyBeaconApi

interface RemoteProvider {
    fun provideRemote(): CurrencyBeaconApi
}