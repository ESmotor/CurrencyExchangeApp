package com.itskidan.currencyexchangeapp.di.modules

import com.itskidan.core_impl.database.MainRepository
import com.itskidan.currencyexchangeapp.domain.Interactor
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun provideInteractor(mainRepository: MainRepository): Interactor {
        return Interactor(mainRepository)
    }
}