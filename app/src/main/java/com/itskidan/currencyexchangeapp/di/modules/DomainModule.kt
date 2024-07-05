package com.itskidan.currencyexchangeapp.di.modules

import android.content.Context
import com.itskidan.core_impl.MainRepository
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils.createCurrencyNameMap
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DomainModule(context: Context) {
    @Provides
    fun provideInteractor(mainRepository: MainRepository): Interactor {
        return Interactor(mainRepository)
    }
    @Provides
    @Singleton
    fun provideCurrencyNameMap(context: Context): Map<String, String> {
        return createCurrencyNameMap(context)
    }
}