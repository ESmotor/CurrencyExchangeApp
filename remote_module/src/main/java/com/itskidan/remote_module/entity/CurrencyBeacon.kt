package com.itskidan.remote_module.entity

data class CurrencyBeacon(
    val date : String,
    val base : String,
    val rates :  Map<String, Double?>
)