package com.itskidan.currencyexchangeapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.domain.Interactor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel : ViewModel() {
    @Inject
    lateinit var interactor: Interactor
    val actualRatesCodeList: StateFlow<List<String>>
        get() = interactor.getActiveCurrencyList()

    private val _totalBalanceCodeList = MutableStateFlow<List<String>>(emptyList())
    val totalBalanceCodeList: StateFlow<List<String>> = _totalBalanceCodeList
    val totalBalanceSelectedCurrency: MutableStateFlow<String>
        get() = interactor.getSelectedTotalBalanceCurrency()

    init {
        App.instance.dagger.inject(this)
        viewModelScope.launch {
            interactor.getTotalBalanceCurrencyList().collect { list ->
                _totalBalanceCodeList.value = list.sortedBy { it.id }.map { it.currencyCode }
            }
        }
    }
}