package com.itskidan.currencyexchangeapp.ui.addcurrency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCurrencyScreenViewModel : ViewModel() {
    lateinit var databaseFromDB: Flow<List<Currency>>

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var currencyNameMap: Map<String, String>

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch { databaseFromDB = interactor.getAllDatabase() }
    }

    fun createNewCurrency(currencyCode: String): Currency {
        return Currency(
            id = 0,
            currencyCode = currencyCode,
            currencyName = getCurrencyName(currencyCode),
            currencyFlagId = getCurrencyFlag(currencyCode),
            currencyAskValue = -1.0,
            currencyBidValue = -1.0
        )
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNameMap[currencyCode] ?: "Unknown Currency"
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return CurrencyUtils.currencyFlagMap[currencyCode] ?: 0
    }

    fun getOtherCurrenciesList(selectedCurrencyList: List<Currency>): List<String> {
        val selectedCurrencyCodeSet = selectedCurrencyList.mapTo(mutableSetOf()) { it.currencyCode }
        val otherCurrenciesList =
            CurrencyUtils.currencyFlagMap.keys.filter { it !in selectedCurrencyCodeSet }
        return otherCurrenciesList.sorted()
    }

    suspend fun updateDatabase(newCurrenciesList: MutableList<Currency>) {
        newCurrenciesList.forEachIndexed { index, currency -> currency.id = index }

        val newCurrenciesMap = newCurrenciesList.withIndex().associate { (index, currency) ->
            currency.currencyCode to index
        }

        val oldCurrenciesList = databaseFromDB.first().toMutableList()

        oldCurrenciesList.forEach { oldCurrency ->
            newCurrenciesMap[oldCurrency.currencyCode]?.let { indexInNewList ->
                newCurrenciesList[indexInNewList].currencyAskValue = oldCurrency.currencyAskValue
                newCurrenciesList[indexInNewList].currencyBidValue = oldCurrency.currencyBidValue
            }
        }
        interactor.updateDatabase(newCurrenciesList)
    }
}