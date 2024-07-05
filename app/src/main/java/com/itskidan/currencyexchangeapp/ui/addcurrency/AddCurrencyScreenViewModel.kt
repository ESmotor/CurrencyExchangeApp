package com.itskidan.currencyexchangeapp.ui.addcurrency

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import javax.inject.Inject

class AddCurrencyScreenViewModel : ViewModel() {
    //    lateinit var databaseFromDB: Flow<List<Currency>>
    val currencyCodeList = listOf(
        "USD",
        "RUB",
        "CHF",
        "EUR",
        "CNY",
        "TRY",
        "KZT",
        "BRL",
        "JPY",
        "GBP",
        "AED",
        "AUD",
        "CAD"
    )
    val activeCurrencyList = listOf("USD", "RUB", "CHF", "EUR")
    val passiveCurrencyList = currencyCodeList.filter { !activeCurrencyList.contains(it) }

    var dataListActive: SnapshotStateList<Currency>
    var dataListPassive: MutableList<Currency>

    @Inject
    lateinit var interactor: Interactor

    @Inject
    lateinit var currencyNameMap: Map<String, String>

    init {
        App.instance.dagger.inject(this)
//        viewModelScope.launch { databaseFromDB = interactor.getCurrencyFromDB() }
        dataListActive = createDataListActive(activeCurrencyList)
        dataListPassive = createDataListPassive(passiveCurrencyList)
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNameMap.get(currencyCode) ?: ""
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return CurrencyUtils.currencyFlagMap[currencyCode] ?: 0
    }

    //example Function
    fun createDataListActive(codeList: List<String>): SnapshotStateList<Currency> {
        return codeList.map { code ->
            Currency(
                currencyCode = code,
                currencyName = getCurrencyName(code),
                currencyFlagId = getCurrencyFlag(code),
                currencyAskValue = 0.0,
                currencyBidValue = 0.0
            )
        }.toMutableStateList()
    }

    fun createDataListPassive(codeList: List<String>): MutableList<Currency> {
        return codeList.map { code ->
            Currency(
                currencyCode = code,
                currencyName = getCurrencyName(code),
                currencyFlagId = getCurrencyFlag(code),
                currencyAskValue = 0.0,
                currencyBidValue = 0.0
            )
        }.toMutableList()
    }
}