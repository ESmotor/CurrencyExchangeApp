package com.itskidan.currencyexchangeapp.domain

import com.itskidan.core_api.entity.TotalBalanceCurrency
import com.itskidan.core_impl.database.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Interactor @Inject constructor(
    private val repository: MainRepository
) {


    fun getCurrentVersion(): StateFlow<String> = repository.currentVersionApp

    suspend fun updateActiveCurrencyList(newCurrenciesList: List<String>, screen: String) =
        repository.updateActiveCurrencyList(newCurrenciesList, screen)

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

    suspend fun updateDatabase() = repository.updateDatabase()

    //    suspend fun updateDatabase(availableCurrencyCodeList: List<String>)=repository.manualUpdateDatabase(availableCurrencyCodeList)
    suspend fun updateTotalBalanceCurrency(currency: TotalBalanceCurrency) =
        repository.updateTotalBalanceCurrency(currency)

    fun getCurrencyCodeList(): List<String> = repository.availableCurrencyCodeList
    fun getActiveCurrencyList() = repository.activeCurrencyList
    fun getTotalBalanceCurrencyList(): Flow<List<TotalBalanceCurrency>> =
        repository.getTotalBalanceCurrencyList()

    fun getLastUpdateCurrencyRates(): StateFlow<Long> = repository.lastUpdateTimeRates
    fun getCurrencyNamesMap(): Map<String, String> = repository.getCurrencyNamesMap()
    fun getDefaultCurrencyName(): String = repository.getDefaultCurrencyName()
    fun getCurrencyFlagsMap(): Map<String, Int> = repository.getCurrencyFlagsMap()
    fun getDefaultCurrencyFlag(): Int = repository.getDefaultCurrencyFlag()
    suspend fun saveSelectedTotalBalanceCurrency(code: String) {
        repository.saveSelectedTotalBalanceCurrency(code)
    }

    fun getSelectedTotalBalanceCurrency() = repository.totalAmountCurrency
    suspend fun updateTotalBalanceCurrencyByCode(code: String, value: Double) {
        repository.updateTotalBalanceCurrencyByCode(code, value)
    }

    suspend fun updateTotalBalanceCurrencyList(oldCurrencyCode: String, newCurrencyCode: String) {
        repository.updateTotalBalanceCurrencyList(oldCurrencyCode, newCurrencyCode)
    }
}