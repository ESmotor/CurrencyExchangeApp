package com.itskidan.core_impl.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itskidan.core_api.DatabaseContract
import com.itskidan.core_api.dao.CurrencyDao
import com.itskidan.core_api.entity.Currency
import com.itskidan.core_api.entity.TotalBalanceCurrency

@Database(entities = [Currency::class,TotalBalanceCurrency::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase(), DatabaseContract {
    abstract override fun currencyDao(): CurrencyDao
}