package com.itskidan.currencyexchangeapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.utils.Constants
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeScreenViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    lateinit var databaseFromDB: Flow<List<Currency>>

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch { databaseFromDB = interactor.getCurrencyFromDB() }
    }


    fun putCurrencyExample() {
        viewModelScope.launch {
            val currencyCode = "BRL"
            val currencyName = "Brazilian Real"
            val currencyFlagId = CurrencyUtils.currencyFlagMap[currencyCode]!!
            val currencyBidValue = (10000..20000).random() / 100.0
            val currencyAskValue = currencyBidValue + (0..200).random() / 100.0
            interactor.putCurrencyToDB(
                Currency(
                    currencyCode = currencyCode,
                    currencyName = currencyName,
                    currencyFlagId = currencyFlagId,
                    currencyAskValue = currencyAskValue,
                    currencyBidValue = currencyBidValue
                )
            )
        }
    }
}