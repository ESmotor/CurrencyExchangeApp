package com.itskidan.currencyexchangeapp.ui.addcurrency

import androidx.lifecycle.ViewModel
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AddCurrencyScreenViewModel : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    private val currencyNamesMap : Map<String,String>
        get() = interactor.getCurrencyNamesMap()
    private val currencyFlagsMap : Map<String,Int>
        get() = interactor.getCurrencyFlagsMap()

    private val currencyCodeList: List<String>
        get() = interactor.getCurrencyCodeList()

    var activeCurrencyList: StateFlow<List<String>>


    init {
        App.instance.dagger.inject(this)
        activeCurrencyList = interactor.getActiveCurrencyList()
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNamesMap[currencyCode]?:interactor.getDefaultCurrencyName()
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode]?: interactor.getDefaultCurrencyFlag()
    }

    fun getOtherCurrenciesList(selectedCurrencyList: List<String>): List<String> {
        return currencyCodeList.filter { it !in selectedCurrencyList }.sorted()
    }

    fun filterBySearch(incomingList: List<String>, searchText: String): List<String> {
        return incomingList.filter { currencyCode ->
            currencyCode.contains(searchText, ignoreCase = true)
                    || getCurrencyName(currencyCode).contains(searchText, ignoreCase = true)
        }
    }
    suspend fun updateActiveCurrencyList(newCurrenciesList: List<String>) =
        interactor.updateActiveCurrencyList(newCurrenciesList)

}