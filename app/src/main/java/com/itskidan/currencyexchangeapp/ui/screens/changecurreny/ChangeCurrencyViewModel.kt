package com.itskidan.currencyexchangeapp.ui.screens.changecurreny

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class ChangeCurrencyViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor

    private val currencyNamesMap: Map<String, String>
        get() = interactor.getCurrencyNamesMap()

    private val currencyFlagsMap: Map<String, Int>
        get() = interactor.getCurrencyFlagsMap()

    private val currencyCodeList: List<String>
        get() = interactor.getCurrencyCodeList()

    private val activeCurrencyList: StateFlow<List<String>>
        get() = interactor.getActiveCurrencyList()

    private var interstitialAd: InterstitialAd? = null

    init {
        App.instance.dagger.inject(this)
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNamesMap[currencyCode] ?: interactor.getDefaultCurrencyName()
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return currencyFlagsMap[currencyCode] ?: interactor.getDefaultCurrencyFlag()
    }

    fun reorderCurrencyList(currencyCode: String): List<String> {
        val mutableCurrencyCodes = currencyCodeList.sorted().toMutableList()
        mutableCurrencyCodes.remove(currencyCode)
        mutableCurrencyCodes.add(0, currencyCode)
        return mutableCurrencyCodes
    }

    fun filterBySearch(incomingList: List<String>, searchText: String): List<String> {
        return incomingList.filter { currencyCode ->
            currencyCode.contains(searchText, ignoreCase = true)
                    || getCurrencyName(currencyCode).contains(searchText, ignoreCase = true)
        }
    }

    private suspend fun saveSelectedLastState(code: String, value: String) {
        interactor.saveSelectedLastState(code, value)
    }

    private suspend fun saveSelectedTotalBalanceCurrency(code: String) {
        interactor.saveSelectedTotalBalanceCurrency(code)
    }

    private suspend fun updateActiveCurrencyList(
        oldCurrencyCode: String,
        newCurrencyCode: String,
        screen: String
    ) {
        val existActiveCurrencyList = activeCurrencyList.first().toMutableList()
        val indexOld = existActiveCurrencyList.indexOf(oldCurrencyCode)
        if (oldCurrencyCode != newCurrencyCode) {
            if (existActiveCurrencyList.contains(newCurrencyCode)) {
                val indexNew = existActiveCurrencyList.indexOf(newCurrencyCode)
                val temp = existActiveCurrencyList[indexOld]
                existActiveCurrencyList[indexOld] = existActiveCurrencyList[indexNew]
                existActiveCurrencyList[indexNew] = temp
            } else {
                existActiveCurrencyList[indexOld] = newCurrencyCode
            }
        }
        interactor.updateActiveCurrencyList(existActiveCurrencyList, screen)
    }

    suspend fun onCurrencyClick(
        isFocused: Boolean,
        oldCurrencyCode: String,
        newCurrencyCode: String,
        oldCurrencyValue: String,
        locationOfRequest: String,
    ) {
        when (locationOfRequest) {
            Constants.ACTUAL_RATES_LIST_TO_CHANGE_CURRENCY -> {
                if (isFocused) {
                    saveSelectedLastState(newCurrencyCode, oldCurrencyValue)
                }
                updateActiveCurrencyList(
                    oldCurrencyCode = oldCurrencyCode,
                    newCurrencyCode = newCurrencyCode,
                    screen = Constants.ACTUAL_RATES_ACTIVE_CURRENCIES_LIST
                )
            }

            Constants.TOTAL_BALANCE_LIST_TO_CHANGE_CURRENCY -> {
                Timber.tag("MyLog").d("TOTAL_BALANCE")
                interactor.updateTotalBalanceCurrencyList(oldCurrencyCode,newCurrencyCode)
            }

            Constants.TOTAL_BALANCE_SELECTED_TO_CHANGE_CURRENCY -> {
                Timber.tag("MyLog").d("TOTAL_BALANCE_ACTIVE")
                saveSelectedTotalBalanceCurrency(newCurrencyCode)
            }

        }


    }

    fun loadInterstitialAd(context: Context) {
        InterstitialAd.load(context,
            Constants.INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    interstitialAd = null
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    super.onAdLoaded(p0)
                    interstitialAd = p0
                }
            })
    }

    fun showInterstitialAd(context: Context, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd!!.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                        interstitialAd = null
                    }

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        interstitialAd = null

                        loadInterstitialAd(context)
                        onAdDismissed()

                    }
                }
            interstitialAd!!.show(context as Activity)
        }
    }
}