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
    suspend fun putCurrencyToDatabase(currency: Currency) {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.insertCurrencyToDB(currency)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateDatabase(currencyList: List<Currency>) {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.clearDatabase()
                currencyDao.insertAll(currencyList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteCurrencyByCodeFromDatabase(currencyCode: String) {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.deleteCurrencyByCodeFromDB(currencyCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearDatabase() {
        try {
            withContext(Dispatchers.IO) {
                currencyDao.clearDatabase()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllDatabase(): Flow<List<Currency>> {
        return currencyDao.getCachedCurrencyFromDB()
    }

}