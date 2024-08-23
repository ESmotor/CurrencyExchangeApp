package com.itskidan.currencyexchangeapp.ui.screens.actualexchangerates

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.core_impl.utils.Constants
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ActualExchangeRatesViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    val activeCurrencyCodeList: StateFlow<List<String>>
        get() = interactor.getActiveCurrencyList()
    private val currencyFlagsMap: Map<String, Int>
        get() = interactor.getCurrencyFlagsMap()
    val ratesFromDatabase: Flow<Map<String, Double>>
        get() = interactor.getRatesFromDatabase()
    val lastUpdateTimeRates: StateFlow<Long>
        get() = interactor.getLastUpdateCurrencyRates()

    private var currentInput = Pair("", "")
    private val currencyCodeList: List<String>
        get() = interactor.getCurrencyCodeList()

    private val _activeCurrencyRates = MutableStateFlow<Map<String, String>>(emptyMap())
    val activeCurrencyRates: MutableStateFlow<Map<String, String>> get() = _activeCurrencyRates

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch(Dispatchers.IO) {
            updateDatabaseRates()
        }
    }

    fun updateCurrentInput(code: String, value: String) {
        currentInput = Pair(code, value.ifEmpty { "0" })
    }

    fun getCurrentInput(): Pair<String, String> {
        return currentInput
    }

    suspend fun updateActiveCurrencyRates() {
        val (inputCode, inputValue) = currentInput
        val inputDoubleValue = inputValue.toDoubleOrNull() ?: 0.0
        val ratesMap = ratesFromDatabase.first()
        val activeCurrencyCodeList = activeCurrencyCodeList.first()

        val resultCalculatedRatesMap = activeCurrencyCodeList.associateWith { currencyCode ->
            when {
                currencyCode == inputCode -> inputValue
                else -> {
                    val currentRate = ratesMap[inputCode] ?: return@associateWith "0"
                    val quotedRate = ratesMap[currencyCode] ?: return@associateWith "0"
                    if (currentRate != 0.0 && quotedRate != 0.0) {
                        val calculatedValue = quotedRate / currentRate * inputDoubleValue
                        formatDoubleToString(calculatedValue)
                    } else {
                        "0"
                    }
                }
            }
        }
        _activeCurrencyRates.value = resultCalculatedRatesMap
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode] ?: interactor.getDefaultCurrencyFlag()
    }

    private fun formatDoubleToString(number: Double): String {
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


    // Work with sharedPreferences
    fun saveSelectedLastState(code: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.saveSelectedLastState(code, value)
        }
    }

    fun getLastSelectedState(): Triple<Int, String, String> {
        val (code, value) = interactor.getLastSelectedState()
        val existActiveCurrencyList = activeCurrencyCodeList.value
        val isContainCode = existActiveCurrencyList.contains(code)
        return if (isContainCode) {
            Triple(existActiveCurrencyList.indexOf(code), code, value)
        } else {
            Triple(0, existActiveCurrencyList[0], value)
        }
    }

    fun getFormattedCurrentTime(timeMillis: Long): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = java.time.Instant.ofEpochMilli(timeMillis)
            val localDateTime =
                java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
            val formatter = java.time.format.DateTimeFormatter.ofPattern(
                "HH:mm, MMM d, yyyy",
                Locale.getDefault()
            )
            localDateTime.format(formatter)
        } else {
            val date = Date(timeMillis)
            val simpleDateFormat = SimpleDateFormat("HH:mm, MMM d, yyyy", Locale.getDefault())
            simpleDateFormat.timeZone = TimeZone.getDefault()
            simpleDateFormat.format(date)
        }
    }

    private fun isDatabaseUpdateTime(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = lastUpdateTimeRates.value
        val result =
            currentTime - lastUpdateTime > TimeUnit.MINUTES.toMillis(Constants.MIN_TIME_FOR_UPDATE_DATABASE)
        return result
    }

    suspend fun updateDatabaseRates(codeList: List<String> = currencyCodeList) {
        if (isDatabaseUpdateTime()) {
            interactor.updateDatabase(codeList)
            interactor.saveUpdateTimeCurrencyRates()
        }
//        interactor.updateDatabase(codeList)
//        interactor.saveUpdateTimeCurrencyRates()
        Timber.tag("MyLog").d("method: updateDatabaseRates()")
    }
}