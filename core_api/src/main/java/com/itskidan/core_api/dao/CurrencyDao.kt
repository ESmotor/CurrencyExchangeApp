package com.itskidan.core_api.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.itskidan.core_api.entity.Currency
import com.itskidan.core_api.entity.TotalBalanceCurrency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    // Adding methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCurrencyRatesToDB(currencies: List<Currency>)

    // getting methods
    @Query("SELECT * FROM cached_currency_rates")
    fun getCachedCurrencyRatesFromDB(): Flow<List<Currency>>


    // methods for table cached_total_balance_currencies
    // Adding methods
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCurrenciesList(currencies: List<TotalBalanceCurrency>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCurrency(currency: TotalBalanceCurrency)

    // getting methods
    @Query("SELECT * FROM cached_total_balance_currencies")
    fun getCachedTotalBalanceCurrency(): Flow<List<TotalBalanceCurrency>>

    // removing methods
    @Query("DELETE FROM cached_total_balance_currencies WHERE currencyCode IN (:codes)")
    suspend fun deleteCurrenciesByCodes(codes: List<String>)

    //Clear Database
    @Query("DELETE FROM cached_total_balance_currencies")
    suspend fun clearTotalBalanceDB()
    @Query("DELETE FROM cached_total_balance_currencies WHERE currencyCode = :currencyCode")
    suspend fun deleteCurrencyFromTotalBalanceDB(currencyCode: String)

    //UpdateMethods
    @Query("UPDATE cached_total_balance_currencies SET currencyValue = :currencyValue WHERE currencyCode = :currencyCode")
    suspend fun updateTotalBalanceCurrencyByCode(currencyCode: String, currencyValue: Double): Int
    @Update
    suspend fun updateTotalBalanceCurrency(currency: TotalBalanceCurrency)
    @Update
    suspend fun updateTotalBalanceCurrencyList(currencies: List<TotalBalanceCurrency>)
}

