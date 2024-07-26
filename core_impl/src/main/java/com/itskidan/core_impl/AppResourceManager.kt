package com.itskidan.core_impl

import android.content.Context
import com.itskidan.core_api.ResourceManager
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppResourceManager @Inject constructor(
    private val context: Context
) : ResourceManager {

    override fun getCurrencyCodes(): List<String> {
        Timber.tag("MyLog").d("method: getCurrencyCodes()")
        val inputStream = context.resources.openRawResource(R.raw.available_currency_codes)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.useLines { it.toList() }
    }
    override fun getDefaultCurrencyName(): String = context.getString(R.string.unknown_currency_name)
    override fun getDefaultCurrencyFlag(): Int = R.drawable.flag_vector_unknown
    override fun getCurrencyNamesMap(): Map<String, String> {
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

    override fun getCurrencyFlagsMap(): Map<String, Int> {
        return mapOf(
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
    }




}