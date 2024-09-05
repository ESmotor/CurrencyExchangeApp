package com.itskidan.currencyexchangeapp.ui.screens.totalbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.core_api.entity.TotalBalanceCurrency
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class TotalBalanceViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    private val _activeCurrencyList = MutableStateFlow<List<TotalBalanceCurrency>>(emptyList())
    val activeCurrencyList: StateFlow<List<TotalBalanceCurrency>> = _activeCurrencyList
    private val currencyFlagsMap: Map<String, Int>
        get() = interactor.getCurrencyFlagsMap()
    private val ratesFromDatabase: Flow<Map<String, Double>>
        get() = interactor.getRatesFromDatabase()

    val lastUpdateTimeRates: StateFlow<Long>
        get() = interactor.getLastUpdateCurrencyRates()

    private var currentInput = Pair(TotalBalanceCurrency(0, "USD", 0.0), "")

    private val _totalAmount = MutableStateFlow("0")
    val totalAmount: MutableStateFlow<String> get() = _totalAmount

    val totalAmountCurrency: MutableStateFlow<String>
        get() = interactor.getSelectedTotalBalanceCurrency()

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch(Dispatchers.IO) {
            updateDatabaseRates()
        }
        viewModelScope.launch {
            interactor.getTotalBalanceCurrencyList().collect { list ->
                _activeCurrencyList.value = list.sortedBy { it.id }
            }
        }
    }


    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode] ?: interactor.getDefaultCurrencyFlag()
    }

    suspend fun updateDatabaseRates() {
        interactor.updateDatabase()
    }

    fun updateCurrentInput(currency: TotalBalanceCurrency, value: String) {
        Timber.tag("MyLog").d("updateCurrentInput: $currency, value: $value)")
        currentInput = Pair(currency, value.ifEmpty { "0" })
    }

    suspend fun updateTotalBalanceCurrency(currency: TotalBalanceCurrency, value: String) {
        Timber.tag("MyLog").d("updateTotalBalanceCurrency: $currency, value: $value)")
        currency.currencyValue = value.toDoubleOrNull() ?: 0.0
        interactor.updateTotalBalanceCurrency(currency)
    }

    fun getCurrentInput(): Pair<TotalBalanceCurrency, String> {
        return currentInput
    }

    fun formatDoubleToString(number: Double): String {
        val bigDecimal = BigDecimal(number)
        val isNegative = number < 0
        val absNumber = bigDecimal.abs()
        var result = if (absNumber < BigDecimal.ONE) {
            val strNumber = absNumber.toPlainString().drop(2)
            val zeroCount = strNumber.takeWhile { it == '0' }.length
            val scale = zeroCount + 2
            absNumber.setScale(scale, RoundingMode.HALF_UP).toPlainString()
        } else {
            absNumber.setScale(2, RoundingMode.HALF_UP).toPlainString()
        }

        if (result.endsWith(".00")) result = result.dropLast(3)

        return if (isNegative) "-$result" else result
    }

    suspend fun calculateTotalAmount() {
        val existCurrencyMap =
            _activeCurrencyList.value.associate { it.currencyCode to it.currencyValue }
                .toMutableMap()
        val currencyRatesMap = ratesFromDatabase.first()
        val totalAmountCurrencyRate = currencyRatesMap[totalAmountCurrency.value] ?: 0.0
        var result = 0.0

        existCurrencyMap.forEach { (currencyCode, currencyValue) ->
            val currencyRate = currencyRatesMap[currencyCode] ?: 0.0
            if (currencyRate != 0.0) {
                result += currencyValue / currencyRate
            }
        }
        result *= totalAmountCurrencyRate

        _totalAmount.value = formatDoubleToString(result)
    }


}