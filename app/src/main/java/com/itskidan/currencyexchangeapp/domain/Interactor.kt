package com.itskidan.currencyexchangeapp.domain

import com.itskidan.core_api.entity.Currency
import com.itskidan.core_impl.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Interactor @Inject constructor(
    private val repository: MainRepository,
) {
    // interaction with local database
    suspend fun putCurrencyToDatabase(currency: Currency) = repository.putCurrencyToDatabase(currency)
    suspend fun updateDatabase(currencyList: List<Currency>) = repository.updateDatabase(currencyList)
    fun getAllDatabase(): Flow<List<Currency>> = repository.getAllDatabase()
}