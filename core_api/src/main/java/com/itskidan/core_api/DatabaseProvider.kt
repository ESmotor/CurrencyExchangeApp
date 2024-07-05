package com.itskidan.core_api

import com.itskidan.core_api.dao.CurrencyDao

interface DatabaseProvider {
    fun currencyDao(): CurrencyDao
}