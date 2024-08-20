package com.itskidan.core_impl.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.itskidan.core_api.ResourceManager
import com.itskidan.core_api.providers.ResourceManagerProvider
import com.itskidan.core_api.dao.CurrencyDao
import com.itskidan.core_impl.database.AppDatabase
import com.itskidan.core_impl.database.MainRepository
import com.itskidan.remote_module.api.CurrencyBeaconApi
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
    fun provideResourceManager(resourceManagerProvider: ResourceManagerProvider): ResourceManager {
        return resourceManagerProvider.provideResourceManager()
    }

    @Provides
    @Singleton
    fun provideRepository(
        context: Context,
        currencyDao: CurrencyDao,
        retrofitService: CurrencyBeaconApi,
        resourceManager: ResourceManager,
        sharedPreferences: SharedPreferences
    ) = MainRepository(context,currencyDao, retrofitService,resourceManager,sharedPreferences)


}