package com.itskidan.remote_module

interface RemoteProvider {
    fun provideRemote(): CurrencyBeaconApi
}