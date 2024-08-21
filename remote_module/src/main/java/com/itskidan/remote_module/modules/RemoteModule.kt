package com.itskidan.remote_module.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.itskidan.remote_module.BuildConfig
import com.itskidan.remote_module.api.CurrencyBeaconApi
import com.itskidan.remote_module.api.ApiConstants
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Module(includes = [BindOkHttpClient::class, BindRetrofit::class])
class RemoteModule {
    @Provides
    @Singleton
    fun provideCurrencyBeaconApi(retrofitProvider: RetrofitProvider): CurrencyBeaconApi {
        return retrofitProvider.provideRetrofit(retrofitProvider.provideOkHttpClient())
            .create(CurrencyBeaconApi::class.java)
    }
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

}

@Module(includes = [BindOkHttpClient::class])
interface BindRetrofit {
    @Binds
    @Singleton
    fun bindRetrofitProvider(implementation: RetrofitProviderImpl): RetrofitProvider
}

@Module
interface BindOkHttpClient {
    @Binds
    @Singleton
    fun bindOkHttpClient(okHttpClient: OkHttpClientImpl): OkHttpClient
}

interface RetrofitProvider {
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit
    fun provideOkHttpClient(): OkHttpClient
}


class RetrofitProviderImpl @Inject constructor(
    private val gson: Gson,
) : RetrofitProvider {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    override fun provideOkHttpClient(): OkHttpClient {
        return okHttpClient
    }
}


class OkHttpClientImpl @Inject constructor() : OkHttpClient() {
    // Create a custom client
    init {
        Builder()
            // Configure timeouts for slow Internet
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            // Add a logger
            .addInterceptor(HttpLoggingInterceptor().apply {
                if (BuildConfig.DEBUG) {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            })
            .build()
    }
}