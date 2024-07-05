package com.itskidan.core_impl

import android.content.Context
import androidx.room.Room
import com.itskidan.core_api.dao.CurrencyDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideCurrencyDao(context: Context) =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "currency_db"
        ).build().currencyDao()


    @Provides
    @Singleton
    fun provideRepository(currencyDao: CurrencyDao) = MainRepository(currencyDao)
}