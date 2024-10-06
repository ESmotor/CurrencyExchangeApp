package com.itskidan.currencyexchangeapp.ui.screens.adoutapp

import androidx.lifecycle.ViewModel
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AboutAppViewModel: ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    val version : StateFlow<String>
        get() = interactor.getCurrentVersion()

    val lastUpdateTimeRates: StateFlow<Long>
        get() = interactor.getLastUpdateCurrencyRates()


    init {
        App.instance.dagger.inject(this)
    }
}