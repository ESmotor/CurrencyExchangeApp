package com.itskidan.currencyexchangeapp.di

import com.itskidan.core_api.providers.AppProvider
import com.itskidan.core_api.providers.DatabaseProvider
import com.itskidan.core_api.providers.ResourceManagerProvider
import com.itskidan.core_impl.database.MainRepository
import com.itskidan.currencyexchangeapp.MainActivity
import com.itskidan.currencyexchangeapp.di.modules.AppModule
import com.itskidan.currencyexchangeapp.di.modules.DomainModule
import com.itskidan.currencyexchangeapp.ui.screens.addcurrency.AddCurrencyViewModel
import com.itskidan.currencyexchangeapp.ui.screens.calculator.CalculatorViewModel
import com.itskidan.currencyexchangeapp.ui.screens.changecurreny.ChangeCurrencyViewModel
import com.itskidan.currencyexchangeapp.ui.screens.actualexchangerates.ActualExchangeRatesViewModel
import com.itskidan.currencyexchangeapp.ui.screens.adoutapp.AboutAppViewModel
import com.itskidan.currencyexchangeapp.ui.screens.dismissad.DismissAdViewModel
import com.itskidan.currencyexchangeapp.ui.screens.sendfeedback.SendFeedbackViewModel
import com.itskidan.currencyexchangeapp.ui.screens.settings.SettingsViewModel
import com.itskidan.currencyexchangeapp.ui.screens.totalbalance.TotalBalanceViewModel
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
    fun inject(addCurrencyViewModel: AddCurrencyViewModel)
    fun inject(changeCurrencyViewModel: ChangeCurrencyViewModel)
    fun inject(calculatorViewModel: CalculatorViewModel)
    fun inject(actualExchangeRatesViewModel: ActualExchangeRatesViewModel)
    fun inject(totalBalanceViewModel: TotalBalanceViewModel)
    fun inject(aboutAppViewModel:AboutAppViewModel)
    fun inject(settingsViewModel: SettingsViewModel)
    fun inject(sendFeedbackViewModel: SendFeedbackViewModel)
    fun inject(dismissAdViewModel: DismissAdViewModel)

}