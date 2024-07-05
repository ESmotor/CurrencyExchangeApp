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
    suspend fun putCurrencyToDB(currency: Currency) = repository.putCurrencyToDB(currency)

    fun getCurrencyFromDB(): Flow<List<Currency>> = repository.getCurrencyFromDB()
}