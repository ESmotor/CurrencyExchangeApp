package com.itskidan.currencyexchangeapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.itskidan.core_impl.utils.Constants

@Composable
fun AdBannerView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
        val windowMetrics: WindowMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(it)
        val bounds = windowMetrics.bounds

        var adWidthPixels = it.resources.displayMetrics.widthPixels.toFloat()

        if (adWidthPixels == 0f) {
            adWidthPixels = bounds.width().toFloat()
        }

        val density = it.resources.displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        AdView(it).apply {
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(it, adWidth))
            adUnitId = Constants.BANNER_AD_UNIT_ID
            loadAd(AdRequest.Builder().build())
        }
    })
}