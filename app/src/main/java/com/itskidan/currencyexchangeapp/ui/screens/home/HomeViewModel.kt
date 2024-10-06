package com.itskidan.currencyexchangeapp.ui.screens.home

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.itskidan.currencyexchangeapp.R

class HomeViewModel : ViewModel() {

    fun getIconsForDrawerMenu(): List<ImageVector> {
        return listOf(
            Icons.Default.Star,
            Icons.Default.CurrencyExchange,
            Icons.Default.MonetizationOn,
            Icons.Default.Settings,
            Icons.AutoMirrored.Filled.Message,
            Icons.Default.Info,
        )
    }

    fun getLabelNameForDrawerMenu(item:ImageVector, context:Context):String{
        return when (item.name.substringAfterLast(".")) {
            "Star" -> context.getString(R.string.drawer_menu_label_disable_advertising)
            "CurrencyExchange" -> context.getString(R.string.drawer_menu_label_actual_exchange_rate)
            "MonetizationOn" -> context.getString(R.string.drawer_menu_label_total_balance)
            "AccessTime" -> context.getString(R.string.drawer_menu_label_world_time)
            "Message" -> context.getString(R.string.drawer_menu_label_send_feedback)
            "Info" -> context.getString(R.string.drawer_menu_label_about_the_application)
            "Settings" -> context.getString(R.string.drawer_menu_label_settings)
            else -> context.getString(R.string.drawer_menu_label_more_options)
        }
    }
}

