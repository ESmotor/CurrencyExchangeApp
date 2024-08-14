package com.itskidan.currencyexchangeapp.ui.changecurreny

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChangeCurrencyScreenViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    private val currencyNamesMap: Map<String, String>
        get() = interactor.getCurrencyNamesMap()
    private val currencyFlagsMap: Map<String, Int>
        get() = interactor.getCurrencyFlagsMap()

    private val currencyCodeList: List<String>
        get() = interactor.getCurrencyCodeList()

    private val activeCurrencyList: StateFlow<List<String>>
        get() = interactor.getActiveCurrencyList()

    init {
        App.instance.dagger.inject(this)
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNamesMap[currencyCode] ?: interactor.getDefaultCurrencyName()
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode] ?: interactor.getDefaultCurrencyFlag()
    }

    fun reorderCurrencyList(currencyCode: String): List<String> {
        val mutableCurrencyCodes = currencyCodeList.sorted().toMutableList()
        mutableCurrencyCodes.remove(currencyCode)
        mutableCurrencyCodes.add(0, currencyCode)
        return mutableCurrencyCodes
    }

    fun filterBySearch(incomingList: List<String>, searchText: String): List<String> {
        return incomingList.filter { currencyCode ->
            currencyCode.contains(searchText, ignoreCase = true)
                    || getCurrencyName(currencyCode).contains(searchText, ignoreCase = true)
        }
    }
    fun saveSelectedLastState(code: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.saveSelectedLastState(code, value)
        }
    }
    suspend fun updateActiveCurrencyList(oldCurrencyCode: String, newCurrencyCode: String) {
        val existActiveCurrencyList = activeCurrencyList.first().toMutableList()
        val indexOld = existActiveCurrencyList.indexOf(oldCurrencyCode)
        if (oldCurrencyCode != newCurrencyCode) {
            if (existActiveCurrencyList.contains(newCurrencyCode)) {
                val indexNew = existActiveCurrencyList.indexOf(newCurrencyCode)
                val temp = existActiveCurrencyList[indexOld]
                existActiveCurrencyList[indexOld] = existActiveCurrencyList[indexNew]
                existActiveCurrencyList[indexNew] = temp
            } else {
                existActiveCurrencyList[indexOld] = newCurrencyCode
            }
        }
        interactor.updateActiveCurrencyList(existActiveCurrencyList)
    }
}