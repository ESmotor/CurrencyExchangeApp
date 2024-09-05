package com.itskidan.currencyexchangeapp.ui.screens.addcurrency

import androidx.lifecycle.ViewModel
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddCurrencyViewModel : ViewModel() {

    @Inject
    lateinit var interactor: Interactor

    private val currencyNamesMap: Map<String, String>
        get() = interactor.getCurrencyNamesMap()
    private val currencyFlagsMap: Map<String, Int>
        get() = interactor.getCurrencyFlagsMap()

    private val currencyCodeList: List<String>
        get() = interactor.getCurrencyCodeList()

    init {
        App.instance.dagger.inject(this)
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNamesMap[currencyCode] ?: interactor.getDefaultCurrencyName()
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode] ?: interactor.getDefaultCurrencyFlag()
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

    suspend fun updateActiveCurrencyList(
        newCurrenciesList: List<String>,
        locationOfRequest: String
    ) {
        when (locationOfRequest) {
            Constants.ACTUAL_RATES_KEYBOARD_TO_ADD_CURRENCY -> {
                interactor.updateActiveCurrencyList(
                    newCurrenciesList,
                    Constants.ACTUAL_RATES_ACTIVE_CURRENCIES_LIST
                )
            }
            Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY->{
                interactor.updateActiveCurrencyList(
                    newCurrenciesList,
                    Constants.TOTAL_BALANCE_ACTIVE_CURRENCIES_LIST
                )
            }
        }


    }

    suspend fun getSelectedCurrencyList(locationOfRequest: String): List<String> {
        return when (locationOfRequest) {
            Constants.ACTUAL_RATES_KEYBOARD_TO_ADD_CURRENCY -> {
                interactor.getActiveCurrencyList().first()
            }

            Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY -> {
                interactor.getTotalBalanceCurrencyList().first().sortedBy {it.id}.map { it.currencyCode }
            }

            else -> listOf()
        }
    }
}