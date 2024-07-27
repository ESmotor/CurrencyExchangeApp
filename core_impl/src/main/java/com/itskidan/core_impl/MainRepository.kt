package com.itskidan.core_impl

import android.content.Context
import android.content.SharedPreferences
import com.itskidan.core_api.ResourceManager
import com.itskidan.core_api.dao.CurrencyDao
import com.itskidan.core_api.entity.Currency
import com.itskidan.remote_module.CurrencyBeaconApi
import com.itskidan.remote_module.entity.API
import com.itskidan.remote_module.entity.CurrencyBeacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    val availableCurrencyCodeList: List<String>


    init {
        availableCurrencyCodeList = getCurrencyCodeList()
        _activeCurrencyList.value = getActiveCurrencyList()
        _lastUpdateTimeRates.value = getUpdateTimeCurrencyRates()
    }

    // Methods for working with  Active Currency List
    private suspend fun saveActiveCurrencyList() {
        try {
            withContext(Dispatchers.IO) {
                val activeCurrencyString = _activeCurrencyList.value.joinToString(",")
                sharedPreferences.edit()
                    .putString(Constants.HOME_SCREEN_ACTIVE_CURRENCIES, activeCurrencyString)
                    .apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getActiveCurrencyList(): List<String> {
        return try {
            val defaultValue = Constants.DEFAULT_VALUE_FOR_ACTIVE_CURRENCIES
            val activeCurrencyString = sharedPreferences.getString(
                Constants.HOME_SCREEN_ACTIVE_CURRENCIES,
                defaultValue
            ) ?: defaultValue
            parseCurrencyString(activeCurrencyString)
        } catch (e: Exception) {
            e.printStackTrace()
            parseCurrencyString(Constants.DEFAULT_VALUE_FOR_ACTIVE_CURRENCIES)
        }
    }

    suspend fun updateActiveCurrencyList(newCurrenciesList: List<String>) {
        _activeCurrencyList.value = newCurrenciesList
        try {
            withContext(Dispatchers.IO) {
                saveActiveCurrencyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun parseCurrencyString(currencyString: String): List<String> {
        return currencyString.replace("\"", "").split(",").map { it.trim() }
    }


    // Methods for working with a database

    suspend fun updateDatabase(availableCurrencyCodeList: List<String>) {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.clearDatabase()
                var updatedCurrencyRates: Map<String, Double?> = emptyMap()
                val result = getRatesFromApi(availableCurrencyCodeList)
                result.onSuccess { currencyBeacon ->
                    updatedCurrencyRates = currencyBeacon.rates
                }.onFailure {
                    Timber.tag("ErrorsLog").e(it)
                }
                val currencyList =
                    prepareCurrencyListForDB(availableCurrencyCodeList, updatedCurrencyRates)
                currencyDao.insertAll(currencyList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun manualUpdateDatabase(availableCurrencyCodeList: List<String>) {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.clearDatabase()
                val updatedCurrencyRates: Map<String, Double?> =
                    availableCurrencyCodeList.associateWith { (100..10000).random()/100.0 }
                val currencyList =
                    prepareCurrencyListForDB(availableCurrencyCodeList, updatedCurrencyRates)
                currencyDao.insertAll(currencyList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
        return currencyDao.getCachedCurrencyFromDB()
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

    suspend fun saveSelectedPositionAndValue(position: Int, value: String) {
        try {
            withContext(Dispatchers.IO) {
                Timber.tag("MyLog")
                    .d("method: saveSelectedPositionAndValueInList($position,$value)")
                val newValue = if (value == "0." || value == "") "0" else value
                sharedPreferences
                    .edit()
                    .putInt(Constants.HOME_SCREEN_LIST_POSITION, position)
                    .putString(Constants.HOME_SCREEN_POSITION_VALUE, newValue)
                    .apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    suspend fun saveUpdateTimeCurrencyRates() {
        try {
            withContext(Dispatchers.IO) {
                Timber.tag("MyLog").d("method: saveUpdateTimeCurrencyRates()")
                val updateTime = System.currentTimeMillis()
                sharedPreferences
                    .edit()
                    .putLong(
                        Constants.HOME_SCREEN_LAST_UPDATE_TIME,
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

    private fun getUpdateTimeCurrencyRates(): Long {
        Timber.tag("MyLog").d("method: getUpdateTimeCurrencyRates()")
        return sharedPreferences.getLong(
            Constants.HOME_SCREEN_LAST_UPDATE_TIME,
            System.currentTimeMillis()
        )
    }

    fun getSelectedPositionAndValue(): Pair<Int, String> {
        val index = sharedPreferences.getInt(Constants.HOME_SCREEN_LIST_POSITION, 0)
        val value = sharedPreferences.getString(Constants.HOME_SCREEN_POSITION_VALUE, "1") ?: "1"
        Timber.tag("MyLog").d("method: loadSelectedPositionAndValue($index,$value)")
        return Pair(index, value)
    }

    fun getCurrencyNamesMap(): Map<String, String> = resourceManager.getCurrencyNamesMap()
    fun getDefaultCurrencyName(): String = resourceManager.getDefaultCurrencyName()
    fun getCurrencyFlagsMap(): Map<String, Int> = resourceManager.getCurrencyFlagsMap()
    fun getDefaultCurrencyFlag(): Int = resourceManager.getDefaultCurrencyFlag()
    fun getCurrencyCodeList(): List<String> = resourceManager.getCurrencyCodes()

}