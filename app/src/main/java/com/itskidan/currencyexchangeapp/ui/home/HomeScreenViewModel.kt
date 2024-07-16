package com.itskidan.currencyexchangeapp.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.JoinFull
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radio
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeScreenViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    @Inject
    lateinit var currencyNameMap: Map<String, String>

    lateinit var databaseFromDB: Flow<List<Currency>>

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch { databaseFromDB = interactor.getAllDatabase() }
    }

    fun getCurrencyName(currencyCode: String): String {
        return currencyNameMap[currencyCode] ?: "Unknown Currency"
    }

    fun getCurrencyFlag(currencyCode: String): Int {
        return CurrencyUtils.currencyFlagMap[currencyCode] ?: 0
    }
    fun getIconsForDrawerMenu (): List<ImageVector>{
        return listOf(
            Icons.Default.AccountCircle,
            Icons.Default.Bookmarks,
            Icons.Default.CalendarMonth,
            Icons.Default.Dashboard,
            Icons.Default.Email,
            Icons.Default.Favorite,
            Icons.Default.Group,
            Icons.Default.Headphones,
            Icons.Default.Image,
            Icons.Default.JoinFull,
            Icons.Default.Keyboard,
            Icons.Default.Laptop,
            Icons.Default.Map,
            Icons.Default.Navigation,
            Icons.Default.Outbox,
            Icons.Default.PushPin,
            Icons.Default.QrCode,
            Icons.Default.Radio,
        )
    }
}