package com.itskidan.remote_module.entity

import com.google.gson.annotations.SerializedName

class CurrencyBeaconResultsDto(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("rates")
    val rates: Map<String, Double?>,
    @SerializedName("response")
    val response: Response
)
data class Meta(
    @SerializedName("code")
    val code: String,
    @SerializedName("disclaimer")
    val disclaimer: String,
)
data class Response(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: Map<String, Double?>,
)