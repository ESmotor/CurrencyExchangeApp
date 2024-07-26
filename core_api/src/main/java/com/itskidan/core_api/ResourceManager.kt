package com.itskidan.core_api

interface ResourceManager {
    fun getCurrencyCodes(): List<String>
    fun getCurrencyNamesMap(): Map<String, String>
    fun getDefaultCurrencyName():String
    fun getCurrencyFlagsMap(): Map<String, Int>
    fun getDefaultCurrencyFlag():Int
}