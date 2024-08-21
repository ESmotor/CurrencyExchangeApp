package com.itskidan.currencyexchangeapp.di

import com.itskidan.core_api.providers.AppProvider
import com.itskidan.core_api.providers.DatabaseProvider
import com.itskidan.core_api.providers.ResourceManagerProvider
import com.itskidan.core_impl.database.MainRepository
import com.itskidan.currencyexchangeapp.MainActivity
import com.itskidan.currencyexchangeapp.di.modules.AppModule
import com.itskidan.currencyexchangeapp.di.modules.DomainModule
import com.itskidan.currencyexchangeapp.ui.addcurrency.AddCurrencyScreenViewModel
import com.itskidan.currencyexchangeapp.ui.calculator.CalculatorScreenViewModel
import com.itskidan.currencyexchangeapp.ui.changecurreny.ChangeCurrencyScreenViewModel
import com.itskidan.currencyexchangeapp.ui.home.HomeScreenViewModel
import com.itskidan.remote_module.providers.RemoteProvider
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [RemoteProvider::class, DatabaseProvider::class, AppProvider::class, ResourceManagerProvider::class],
    modules = [DomainModule::class, AppModule::class]
)
interface AppComponent {

    fun inject(mainRepository: MainRepository)
    fun inject(mainActivity: MainActivity)
    fun inject(homeScreenViewModel: HomeScreenViewModel)
    fun inject(addCurrencyScreenViewModel: AddCurrencyScreenViewModel)
    fun inject(changeCurrencyScreenViewModel: ChangeCurrencyScreenViewModel)
    fun inject(calculatorScreenViewModel: CalculatorScreenViewModel)

}