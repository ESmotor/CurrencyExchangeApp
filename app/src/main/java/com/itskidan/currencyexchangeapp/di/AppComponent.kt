package com.itskidan.currencyexchangeapp.di

import com.itskidan.core_api.AppProvider
import com.itskidan.core_api.DatabaseProvider
import com.itskidan.core_impl.MainRepository
import com.itskidan.currencyexchangeapp.MainActivity
import com.itskidan.currencyexchangeapp.di.modules.DomainModule
import com.itskidan.currencyexchangeapp.ui.addcurrency.AddCurrencyScreenViewModel
import com.itskidan.currencyexchangeapp.ui.home.HomeScreenViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [ DatabaseProvider::class, AppProvider::class],
    modules = [
        DomainModule::class,
    ]
)
interface AppComponent {
    fun inject(mainRepository: MainRepository)
    fun inject(mainActivity: MainActivity)
    fun inject(homeScreenViewModel: HomeScreenViewModel)
    fun inject(addCurrencyScreenViewModel: AddCurrencyScreenViewModel)

}