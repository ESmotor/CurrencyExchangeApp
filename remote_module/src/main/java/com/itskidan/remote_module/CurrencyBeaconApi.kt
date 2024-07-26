package com.itskidan.remote_module

import com.itskidan.remote_module.entity.CurrencyBeaconResultsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyBeaconApi {
    @GET("latest")
    suspend fun getRate(
        @Query("api_key") apiKey: String,
        @Query("base") base: String,
        @Query("symbols") quoted: String
    ): Response<CurrencyBeaconResultsDto>
}