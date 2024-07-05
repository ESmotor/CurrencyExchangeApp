package com.itskidan.currencyexchangeapp.utils

import android.content.Context
import com.itskidan.currencyexchangeapp.R

object CurrencyUtils {
    // currency flag resources
//    val currencyFlagMap = mapOf(
//        "USD" to R.drawable.flag_usd,
//        "RUB" to R.drawable.flag_rub,
//        "CHF" to R.drawable.flag_chf,
//        "EUR" to R.drawable.flag_eur,
//        "CNY" to R.drawable.flag_cny,
//        "TRY" to R.drawable.flag_try,
//        "KZT" to R.drawable.flag_kzt,
//        "BRL" to R.drawable.flag_brl,
//        "JPY" to R.drawable.flag_jpy,
//        "GBP" to R.drawable.flag_gbp,
//    )
    val currencyFlagMap = mapOf(
        "USD" to R.drawable.flag_vector_usd,
        "RUB" to R.drawable.flag_vector_rub,
        "CHF" to R.drawable.flag_vector_chf,
        "EUR" to R.drawable.flag_vector_eur,
        "CNY" to R.drawable.flag_vector_cny,
        "TRY" to R.drawable.flag_vector_try,
        "KZT" to R.drawable.flag_vector_kzt,
        "BRL" to R.drawable.flag_vector_brl,
        "JPY" to R.drawable.flag_vector_jpy,
        "GBP" to R.drawable.flag_vector_gbp,
        "AED" to R.drawable.flag_vector_aed,
        "AUD" to R.drawable.flag_vector_aud,
        "CAD" to R.drawable.flag_vector_cad,
    )

    // currency name resources
    fun createCurrencyNameMap (context: Context): Map<String, String> {
        return mapOf(
            "USD" to context.getString(R.string.united_sate_dollar),
            "RUB" to context.getString(R.string.russian_ruble),
            "CHF" to context.getString(R.string.swiss_franc),
            "EUR" to context.getString(R.string.european_euro),
            "CNY" to context.getString(R.string.chinese_yuan),
            "TRY" to context.getString(R.string.turkish_lira),
            "KZT" to context.getString(R.string.kazakhstan_tenge),
            "BRL" to context.getString(R.string.brazilian_real),
            "JPY" to context.getString(R.string.japanese_yen),
            "GBP" to context.getString(R.string.british_pound_sterling),
            "AED" to context.getString(R.string.united_arab_emirates_dirham),
            "AUD" to context.getString(R.string.australian_dollar),
            "CAD" to context.getString(R.string.canadian_dollar),
        )
    }
}