package com.itskidan.currencyexchangeapp.domain

import com.itskidan.core_impl.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Interactor @Inject constructor(
    private val repository: MainRepository
) {

    suspend fun updateActiveCurrencyList(newCurrenciesList: List<String>) =
        repository.updateActiveCurrencyList(newCurrenciesList)

    suspend fun saveSelectedLastState(code: String, value: String) =
        repository.saveSelectedLastState(code, value)

    fun getLastSelectedState(): Pair<String, String> = repository.getLastSelectedState()
    fun getRatesFromDatabase(): Flow<Map<String, Double>> {
        return repository.getRatesFromDatabase().map { currencyList ->
            currencyList.associate { currency ->
                currency.currencyCode to currency.currencyRate
            }
        }
    }
    suspend fun updateDatabase(availableCurrencyCodeList: List<String>)=repository.updateDatabase(availableCurrencyCodeList)
//    suspend fun updateDatabase(availableCurrencyCodeList: List<String>)=repository.manualUpdateDatabase(availableCurrencyCodeList)
    fun getCurrencyCodeList(): List<String> = repository.availableCurrencyCodeList
    fun getActiveCurrencyList() = repository.activeCurrencyList
    fun getLastUpdateCurrencyRates(): StateFlow<Long> = repository.lastUpdateTimeRates
    fun getCurrencyNamesMap(): Map<String, String> = repository.getCurrencyNamesMap()
    fun getDefaultCurrencyName(): String = repository.getDefaultCurrencyName()
    fun getCurrencyFlagsMap(): Map<String, Int> = repository.getCurrencyFlagsMap()
    fun getDefaultCurrencyFlag(): Int = repository.getDefaultCurrencyFlag()
    suspend fun saveUpdateTimeCurrencyRates()=repository.saveUpdateTimeCurrencyRates()
}