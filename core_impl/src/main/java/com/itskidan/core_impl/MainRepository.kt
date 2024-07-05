package com.itskidan.core_impl

import com.itskidan.core_api.dao.CurrencyDao
import com.itskidan.core_api.entity.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(private val currencyDao: CurrencyDao) {
    // put currency to BataBase
    suspend fun putCurrencyToDB(currency: Currency) {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.insertCurrencyToDB(currency)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrencyFromDB(): Flow<List<Currency>> {
        return currencyDao.getCachedCurrencyFromDB()
    }
}