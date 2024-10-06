package com.itskidan.core_impl.database

import android.content.Context
import android.content.SharedPreferences
import com.itskidan.core_api.ResourceManager
import com.itskidan.core_api.dao.CurrencyDao
import com.itskidan.core_api.entity.Currency
import com.itskidan.core_api.entity.TotalBalanceCurrency
import com.itskidan.core_impl.utils.Constants
import com.itskidan.remote_module.api.API
import com.itskidan.remote_module.api.CurrencyBeaconApi
import com.itskidan.remote_module.entity.CurrencyBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val context: Context,
    private val currencyDao: CurrencyDao,
    private val retrofitService: CurrencyBeaconApi,
    private val resourceManager: ResourceManager,
    private val sharedPreferences: SharedPreferences
) {

    private val _activeCurrencyList = MutableStateFlow<List<String>>(emptyList())
    val activeCurrencyList: StateFlow<List<String>> get() = _activeCurrencyList

    private val _lastUpdateTimeRates = MutableStateFlow(0L)
    val lastUpdateTimeRates: StateFlow<Long> get() = _lastUpdateTimeRates

    private val _currentVersionApp = MutableStateFlow("1.0.1")
    val currentVersionApp: StateFlow<String> get() = _currentVersionApp

    val availableCurrencyCodeList: List<String>

    private val _totalAmountCurrency = MutableStateFlow("USD")
    val totalAmountCurrency: MutableStateFlow<String> get() = _totalAmountCurrency

    init {
        availableCurrencyCodeList = getCurrencyCodeList()
        _activeCurrencyList.value = getActualRatesCurrencyList()
        _lastUpdateTimeRates.value = getUpdateTimeCurrencyRates()
        _totalAmountCurrency.value = getSelectedTotalBalanceCurrency()
    }

    // Methods for working with  Active Currency List
    private suspend fun saveActiveCurrencyList(newCurrencyList: List<String>, screen: String) {
        try {
            withContext(Dispatchers.IO) {
                val activeCurrencyString = newCurrencyList.joinToString(",")
                sharedPreferences.edit()
                    .putString(screen, activeCurrencyString)
                    .apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getActualRatesCurrencyList(): List<String> {
        return try {
            val defaultValue = Constants.DEFAULT_VALUE_FOR_ACTUAL_RATES_CURRENCIES_LIST
            val activeCurrencyString = sharedPreferences.getString(
                Constants.ACTUAL_RATES_ACTIVE_CURRENCIES_LIST,
                defaultValue
            ) ?: defaultValue
            parseCurrencyString(activeCurrencyString)
        } catch (e: Exception) {
            e.printStackTrace()
            parseCurrencyString(Constants.DEFAULT_VALUE_FOR_ACTUAL_RATES_CURRENCIES_LIST)
        }
    }

    fun getTotalBalanceCurrencyList(): Flow<List<TotalBalanceCurrency>> {
        return currencyDao.getCachedTotalBalanceCurrency()
    }

    suspend fun updateActiveCurrencyList(newCurrenciesList: List<String>, screen: String) {
        when (screen) {
            Constants.ACTUAL_RATES_ACTIVE_CURRENCIES_LIST -> {
                _activeCurrencyList.value = newCurrenciesList
            }

            Constants.TOTAL_BALANCE_ACTIVE_CURRENCIES_LIST -> {
                Timber.tag("MyLog").d("newCurrenciesList: $newCurrenciesList")
                if (newCurrenciesList.isEmpty()) {
                    currencyDao.clearTotalBalanceDB()
                } else {
                    val existingCurrencyMap = currencyDao.getCachedTotalBalanceCurrency().first()
                        .associateBy { it.currencyCode }
                        .toMutableMap()
                    val newCurrencySet = newCurrenciesList.toSet()
                    val currenciesToDelete = existingCurrencyMap.keys - newCurrencySet
                    if (currenciesToDelete.isNotEmpty()) {
                        currencyDao.deleteCurrenciesByCodes(currenciesToDelete.toList())
                        existingCurrencyMap.keys.removeAll(currenciesToDelete)
                    }

                    newCurrenciesList.forEachIndexed { index, code ->
                        val currency = existingCurrencyMap[code]
                        if (currency != null) {
                            currency.id = index
                        } else {
                            existingCurrencyMap[code] = TotalBalanceCurrency(index, code, 0.0)
                        }
                    }
                    currencyDao.insertOrUpdateCurrenciesList(existingCurrencyMap.values.toList())
                }
            }
        }

        try {
            withContext(Dispatchers.IO) {
                saveActiveCurrencyList(newCurrenciesList, screen)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun parseCurrencyString(currencyString: String): List<String> {
        return currencyString.replace("\"", "").split(",").map { it.trim() }
    }


    // Methods for working with a database

    private suspend fun updateDatabaseRatesFromApi() {
        try {
            withContext(Dispatchers.IO) {
                var updatedCurrencyRates: Map<String, Double?> = emptyMap()
                val result = getRatesFromApi(availableCurrencyCodeList)
                result.onSuccess { currencyBeacon ->
                    updatedCurrencyRates = currencyBeacon.rates
                }.onFailure {
                    Timber.tag("ErrorsLog").e(it)
                }
                val currencyList =
                    prepareCurrencyListForDB(availableCurrencyCodeList, updatedCurrencyRates)
                currencyDao.insertListCurrencyRatesToDB(currencyList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateTotalBalanceCurrency(currency: TotalBalanceCurrency) {
        currencyDao.updateTotalBalanceCurrency(currency)
    }
//    suspend fun manualUpdateDatabase(availableCurrencyCodeList: List<String>) {
//        try {
//            withContext(Dispatchers.IO) {
//                val updatedCurrencyRates: Map<String, Double?> =
//                    availableCurrencyCodeList.associateWith { (100..10000).random()/100.0 }
//                val currencyList =
//                    prepareCurrencyListForDB(availableCurrencyCodeList, updatedCurrencyRates)
//                currencyDao.insertAll(currencyList)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }


    private fun prepareCurrencyListForDB(
        codeList: List<String>,
        ratesMap: Map<String, Double?>
    ): List<Currency> {
        return codeList.mapIndexed { index, code ->
            val currencyRate = ratesMap.getOrElse(code) { 0.0 } ?: 0.0
            Currency(index, code, currencyRate)
        }
    }

    fun getRatesFromDatabase(): Flow<List<Currency>> {
        return currencyDao.getCachedCurrencyRatesFromDB()
    }


    // Methods for working with remote API

    private suspend fun getRatesFromApi(availableCurrencyCodeList: List<String>): Result<CurrencyBeacon> {
        return try {
            val response = withContext(Dispatchers.IO) {
                retrofitService.getRate(
                    apiKey = API.KEY,
                    base = "USD",
                    quoted = availableCurrencyCodeList.joinToString(",")
                )
            }
            if (response.isSuccessful) {
                val dtoApiResult = response.body()
                dtoApiResult?.let {
                    if (it.meta.code == "200") {
                        val base = dtoApiResult.response.base
                        val date = dtoApiResult.response.date
                        val rates = dtoApiResult.response.rates
                        val currencyBeacon = CurrencyBeacon(date, base, rates)
                        Result.success(currencyBeacon)
                    } else {
                        Result.failure(Exception("Error: API returned code ${it.meta.code}"))
                    }
                } ?: Result.failure(Exception("Error: Response body is null"))
            } else {
                Result.failure(Exception("Error: Network call was not successful, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Exception: ${e.message}"))
        }
    }

    suspend fun saveSelectedLastState(code: String, value: String) {
        try {
            withContext(Dispatchers.IO) {
                sharedPreferences
                    .edit()
                    .putString(Constants.ACTUAL_RATES_LAST_STATE_CODE, code)
                    .putString(Constants.ACTUAL_RATES_LAST_STATE_VALUE, value)
                    .apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private suspend fun saveUpdateTimeCurrencyRates() {
        try {
            withContext(Dispatchers.IO) {
                Timber.tag("MyLog").d("method: saveUpdateTimeCurrencyRates()")
                val updateTime = System.currentTimeMillis()
                sharedPreferences
                    .edit()
                    .putLong(
                        Constants.LAST_UPDATE_TIME,
                        updateTime
                    )
                    .apply()
                _lastUpdateTimeRates.value = updateTime
            }
        } catch (e: Exception) {
            Timber.tag("MyLog").d("ERROR: saveUpdateTimeCurrencyRates()")
            e.printStackTrace()
        }
    }


    fun getLastSelectedState(): Pair<String, String> {
        val code =
            sharedPreferences.getString(Constants.ACTUAL_RATES_LAST_STATE_CODE, "USD") ?: "USD"
        val value = sharedPreferences.getString(Constants.ACTUAL_RATES_LAST_STATE_VALUE, "1") ?: "1"
        return Pair(code, value)
    }


    private fun getUpdateTimeCurrencyRates(): Long {
        Timber.tag("MyLog").d("method: getUpdateTimeCurrencyRates()")
        return sharedPreferences.getLong(
            Constants.LAST_UPDATE_TIME,
            System.currentTimeMillis()
        )
    }

    fun getCurrencyNamesMap(): Map<String, String> = resourceManager.getCurrencyNamesMap()
    fun getDefaultCurrencyName(): String = resourceManager.getDefaultCurrencyName()
    fun getCurrencyFlagsMap(): Map<String, Int> = resourceManager.getCurrencyFlagsMap()
    fun getDefaultCurrencyFlag(): Int = resourceManager.getDefaultCurrencyFlag()
    private fun getCurrencyCodeList(): List<String> = resourceManager.getCurrencyCodes()
    suspend fun saveSelectedTotalBalanceCurrency(code: String) {
        try {
            withContext(Dispatchers.IO) {
                sharedPreferences
                    .edit()
                    .putString(Constants.TOTAL_BALANCE_SELECTED_CURRENCY, code)
                    .apply()
            }
            _totalAmountCurrency.value = code
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getSelectedTotalBalanceCurrency(): String {
        val code =
            sharedPreferences.getString(Constants.TOTAL_BALANCE_SELECTED_CURRENCY, "USD") ?: "USD"
        return code
    }

    suspend fun updateTotalBalanceCurrencyByCode(code: String, value: Double) {
        currencyDao.updateTotalBalanceCurrencyByCode(code, value)
    }

    suspend fun updateTotalBalanceCurrencyList(oldCurrencyCode: String, newCurrencyCode: String) {
        val currencyMap =
            getTotalBalanceCurrencyList().first().associateBy { it.currencyCode }.toMutableMap()

        if (newCurrencyCode == oldCurrencyCode) return

        val oldCurrency = currencyMap[oldCurrencyCode]
        val newCurrency = currencyMap[newCurrencyCode]

        when {
            newCurrency != null && oldCurrency != null -> {
                // Swap IDs between the two currencies
                val tempId = oldCurrency.id
                oldCurrency.id = newCurrency.id
                newCurrency.id = tempId
                currencyDao.updateTotalBalanceCurrencyList(listOf(oldCurrency, newCurrency))
            }

            oldCurrency != null -> {
                // Update the old currency with the new code and insert it
                currencyDao.deleteCurrencyFromTotalBalanceDB(oldCurrencyCode)
                val updatedCurrency = oldCurrency.copy(currencyCode = newCurrencyCode)
                currencyDao.insertOrUpdateCurrency(updatedCurrency)
            }
        }
    }

    suspend fun updateDatabase() {
        val currencyRateUSD = currencyDao.getCurrencyRateByCode("USD")
        val isDatabaseReady = !(currencyRateUSD == null || currencyRateUSD == 0.0)
        if (isDatabaseUpdateTime(Constants.MIN_TIME_FOR_UPDATE_DATABASE) || !isDatabaseReady) {
            updateDatabaseRatesFromApi()
            saveUpdateTimeCurrencyRates()
            Timber.tag("MyLog").d("method: updateDatabaseRatesFromApi()")
        } else {
            val currentTime = System.currentTimeMillis()
            val lastUpdateTime = _lastUpdateTimeRates.value
            val result = currentTime - lastUpdateTime > TimeUnit.MINUTES.toMillis(5L)
            if (result) {
                _lastUpdateTimeRates.value = currentTime
                Timber.tag("MyLog").d("method: NotUpdateDatabaseRatesFromApi(onlyTime)")
            }
        }
    }

    private fun isDatabaseUpdateTime(minutes: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = getUpdateTimeCurrencyRates()
        val result = currentTime - lastUpdateTime > TimeUnit.MINUTES.toMillis(minutes)
        return result
    }

}