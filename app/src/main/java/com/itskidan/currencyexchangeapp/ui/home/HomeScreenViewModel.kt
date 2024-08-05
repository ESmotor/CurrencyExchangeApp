package com.itskidan.currencyexchangeapp.ui.home

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.JoinFull
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radio
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.core_impl.Constants
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

class HomeScreenViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    val activeCurrencyList: StateFlow<List<String>>
        get() = interactor.getActiveCurrencyList()
    private val currencyFlagsMap: Map<String, Int>
        get() = interactor.getCurrencyFlagsMap()
    val ratesFromDatabase: Flow<Map<String, Double>>
        get() = interactor.getRatesFromDatabase()
    val lastUpdateTimeRates: StateFlow<Long>
        get() = interactor.getLastUpdateCurrencyRates()

    private var activeCodeList: List<String> = listOf()
    private var activeRatesFromDataBase: Map<String, Double> = mapOf()

    private var _currentInput = Pair("", "")
    private val currencyCodeList: List<String>
        get() = interactor.getCurrencyCodeList()

    private val _calculatedRates = MutableStateFlow<Map<String, String>>(emptyMap())
    val calculatedRates: MutableStateFlow<Map<String, String>> get() = _calculatedRates

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch(Dispatchers.IO) {
            updateDatabaseRates()
        }
    }

    suspend fun updateActiveListAndRates() {
        activeCodeList = activeCurrencyList.value
        activeRatesFromDataBase = ratesFromDatabase.first().filter {
            activeCodeList.contains(it.key)
        }
    }

    fun updateCurrentInput(code: String, value: String) {
        Timber.tag("MyLog").d("method: updateCurrentInput($code,$value)")
        _currentInput = Pair(code, value.ifEmpty { "0.0" })
    }

    fun updateCalculatedRates() {
        Timber.tag("MyLog").d("method: updateCalculatedRates()")
        val (inputCode, inputValue) = _currentInput
        val inputDoubleValue = inputValue.toDoubleOrNull() ?: 0.0
        val ratesMap = activeRatesFromDataBase
        val activeCurrencyCodes = activeCodeList

        val resultCalculatedRatesMap = activeCurrencyCodes.associateWith { currencyCode ->
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
        _calculatedRates.value = resultCalculatedRatesMap
    }

    fun getCalculatedRateByCode(code: String): String {
        return _calculatedRates.value[code] ?: "0"
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode] ?: interactor.getDefaultCurrencyFlag()
    }

    fun getIconsForDrawerMenu(): List<ImageVector> {
        return listOf(
            Icons.Default.AccountCircle,
            Icons.Default.Bookmarks,
            Icons.Default.CalendarMonth,
            Icons.Default.Dashboard,
            Icons.Default.Email,
            Icons.Default.Favorite,
            Icons.Default.Group,
            Icons.Default.Headphones,
            Icons.Default.Image,
            Icons.Default.JoinFull,
            Icons.Default.Keyboard,
            Icons.Default.Laptop,
            Icons.Default.Map,
            Icons.Default.Navigation,
            Icons.Default.Outbox,
            Icons.Default.PushPin,
            Icons.Default.QrCode,
            Icons.Default.Radio,
        )
    }

    fun validateAndFormatInput(newValue: String): String {
        var newText = newValue
        if (newValue.all { "0123456789,.".contains(it) }) {
            newText = newText.replace(",", ".")
            if (newText.count { it == '.' } <= 1) {
                if (newText.indexOf('.') == 0) {
                    newText = "0$newText"
                }
                if (newText.length >= 2
                    && !newText.contains('.')
                    && newText.startsWith('0')
                ) {
                    newText = newText.drop(1)
                }
            } else {
                newText = newText.dropLast(1)
            }
        } else {
            newText = newText.dropLast(1)
        }
        return newText
    }


    private fun formatDoubleToString(number: Double): String {
        val bigDecimal = BigDecimal(number)
        val isNegative = number < 0
        val absNumber = bigDecimal.abs()

        return if (absNumber < BigDecimal.ONE) {
            val strNumber = absNumber.toPlainString().drop(2)
            val zeroCount = strNumber.takeWhile { it == '0' }.length
            val scale = zeroCount + 2

            var result = absNumber.setScale(scale, RoundingMode.HALF_UP).toPlainString()
            if (result.endsWith(".00")) result = result.dropLast(3)
            if (isNegative) "-$result" else result
        } else {
            var result = absNumber.setScale(2, RoundingMode.HALF_UP).toPlainString()
            if (result.endsWith(".00")) result = result.dropLast(3)
            if (isNegative) "-$result" else result
        }
    }


    // Work with sharedPreferences
    fun saveSelectedPositionAndValue(position: Int, value: String) {
        Timber.tag("MyLog").d("method: saveSelectedPositionAndValueInList($position,$value)")
        viewModelScope.launch(Dispatchers.IO) {
            interactor.saveSelectedPositionAndValue(position, value)
        }
    }

    fun getSelectedPositionAndValue(): Pair<Int, String> {
        val (index, value) = interactor.getSelectedPositionAndValue()
        val validIndex = activeCurrencyList.value.indices.contains(index)
        val correctedIndex = if (validIndex) index else 0
        val correctedValue = if (validIndex) value else "1"
        Timber.tag("MyLog").d("method: getSelectedPositionAndValue($correctedIndex,$correctedValue)")
        return Pair(correctedIndex, correctedValue)
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
        interactor.updateDatabase(codeList)
        interactor.saveUpdateTimeCurrencyRates()


        Timber.tag("MyLog").d("method: updateDatabaseRates()")
    }
}

