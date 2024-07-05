package com.itskidan.core_api

import android.content.Context

interface AppProvider {
    fun provideContext(): Context
}