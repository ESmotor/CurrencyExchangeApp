package com.itskidan.core_api.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itskidan.core_api.entity.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    // Adding methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyToDB(currency: Currency)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<Currency>)

    // getting methods
    @Query("SELECT * FROM cached_currency_rates WHERE currencyCode = :currencyCode")
    suspend fun getCurrencyByCode(currencyCode: String): Currency?
    @Query("SELECT * FROM cached_currency_rates")
    fun getCachedCurrencyFromDB(): Flow<List<Currency>>

    // removing methods
    @Query("DELETE FROM cached_currency_rates")
    suspend fun clearDatabase()
    @Query("DELETE FROM cached_currency_rates WHERE currencyCode = :currencyCode")
    suspend fun deleteCurrencyByCodeFromDB(currencyCode: String)


}

