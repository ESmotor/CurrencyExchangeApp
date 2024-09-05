package com.itskidan.currencyexchangeapp.ui.screens.totalbalance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.core.Utils.TimeUtils
import com.itskidan.core_api.entity.TotalBalanceCurrency
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.ui.components.CurrencyCard
import com.itskidan.currencyexchangeapp.ui.components.KeyboardForTyping
import com.itskidan.currencyexchangeapp.ui.components.TotalBalanceCurrencyCard
import com.itskidan.currencyexchangeapp.ui.components.UpdateBox
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import com.itskidan.currencyexchangeapp.utils.NavigationConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun TotalBalanceScreen(
    viewModel: TotalBalanceViewModel = viewModel(),
    innerPadding: PaddingValues,
    navController: NavHostController,
    scope: CoroutineScope,
) {

    //Loading DataBase
    val totalBalanceCurrenciesList by viewModel.activeCurrencyList.collectAsState(initial = emptyList())
    var isDatabaseReady by remember { mutableStateOf(false) }


    var isRefreshing by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    val isChangeFocus = remember { mutableStateOf(false) }
    val textStateFromKeyboard = remember { mutableStateOf(TextFieldValue("")) }
    val textStateTotalAmount by viewModel.totalAmount.collectAsState(initial = "0")
    val totalAmountCurrency by viewModel.totalAmountCurrency.collectAsState(initial = "USD")

    //Checking for last update time
    val lastUpdateTimeRates by viewModel.lastUpdateTimeRates.collectAsState(initial = 0L)
    var updateTime by remember { mutableStateOf("") }

    LaunchedEffect(lastUpdateTimeRates) {
        if (lastUpdateTimeRates > 0L) {
            updateTime = TimeUtils.getFormattedCurrentTime(lastUpdateTimeRates)
        }
    }

    // Initialize state
    var lastSelectedIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(totalBalanceCurrenciesList) {
        if (totalBalanceCurrenciesList.isNotEmpty()) {
            val lastSelectedValue =
                viewModel.formatDoubleToString(totalBalanceCurrenciesList[lastSelectedIndex].currencyValue)
            textStateFromKeyboard.value =
                TextFieldValue(lastSelectedValue, TextRange(lastSelectedValue.length))
            viewModel.calculateTotalAmount()
            isDatabaseReady = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier
                .weight(3f)
                .fillMaxSize()
                .zIndex(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            UpdateBox(
                isUpdating = isUpdating,
                updateTime = updateTime
            )
        }
        Box(
            modifier = Modifier
                .weight(20f)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            val currencyValue = "0.0"
            val isFocused = false

            TotalBalanceCurrencyCard(
                currencyCode = totalAmountCurrency,
                currencyFlag = viewModel.getCurrencyFlag(totalAmountCurrency),
                textState = textStateTotalAmount,
                onChangeCurrency = {
                    navController.navigate(
                        "${NavigationConstants.CHANGE_CURRENCY}/" +
                                "$totalAmountCurrency/" +
                                "$currencyValue/" +
                                "$isFocused/" +
                                Constants.TOTAL_BALANCE_SELECTED_TO_CHANGE_CURRENCY
                    )
                }
            )
        }
        Box(
            modifier = Modifier
                .weight(38f)
                .fillMaxSize()
                .zIndex(0f)
        ) {
            if (totalBalanceCurrenciesList.isNotEmpty() && isDatabaseReady) {
                PullToRefreshTotalBalance(
                    viewModel = viewModel,
                    totalBalanceCurrenciesList = totalBalanceCurrenciesList,
                    isRefreshing = isRefreshing,
                    isChangeFocus = isChangeFocus.value,
                    textStateFromKeyboard = textStateFromKeyboard.value,
                    lastSelectedIndex = lastSelectedIndex,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            isUpdating = true
                            viewModel.updateDatabaseRates()
                            delay(1000)
                            isRefreshing = false
                            isUpdating = false
                        }
                    },
                    onFocusChange = { index, currency, value ->
                        lastSelectedIndex = index
                        viewModel.updateCurrentInput(currency, value)
                        textStateFromKeyboard.value =
                            TextFieldValue(value, TextRange(0, value.length))
                        isChangeFocus.value = true
                    },
                    onTextChange = { newTextState ->
                        if (isChangeFocus.value) {
                            val text = newTextState.text
                            textStateFromKeyboard.value =
                                TextFieldValue(text, TextRange(0, text.length))
                        } else {
                            textStateFromKeyboard.value = newTextState
                        }
                    },
                    onChangeCurrency = { code, value, isFocused ->
                        Timber.tag("MyLog").d("code:$code, value:$value, isFocused:$isFocused")
                        val currencyCode = code.ifEmpty { "USD" }
                        val currencyValue = value.ifEmpty { "0" }
                        navController.navigate(
                            "${NavigationConstants.CHANGE_CURRENCY}/" +
                                    "$currencyCode/" +
                                    "$currencyValue/" +
                                    "$isFocused/" +
                                    Constants.TOTAL_BALANCE_LIST_TO_CHANGE_CURRENCY
                        )

                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(32f)
                .fillMaxSize()
                .zIndex(1f)
        ) {
            KeyboardForTyping(
                textState = textStateFromKeyboard.value,
                isChangeFocus = isChangeFocus.value,
                onTextChange = { newTextState ->
                    val currentInputCurrency = viewModel.getCurrentInput().first
                    isChangeFocus.value = false
                    textStateFromKeyboard.value = newTextState
                    viewModel.updateCurrentInput(currentInputCurrency, newTextState.text)
                    scope.launch {
                        viewModel.updateTotalBalanceCurrency(
                            currentInputCurrency,
                            newTextState.text
                        )
                        viewModel.calculateTotalAmount()
                    }
                },
                onFocusChange = { value ->
                    if (value) isChangeFocus.value = false
                },
                onAddCurrencies = {
                    navController.navigate(
                        "${NavigationConstants.ADD_CURRENCY}/" +
                                Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY
                    )
                },
                onUpdateRates = {
                    scope.launch {
                        isUpdating = true
                        viewModel.updateDatabaseRates()
                        delay(1000)
                        isUpdating = false
                    }
                },
                onCalcLaunch = {
                    val (currency, rate) = viewModel.getCurrentInput()
                    navController.navigate(
                        "${NavigationConstants.CALCULATOR}/" +
                                "${currency.currencyCode}/" +
                                "${rate.replace(".", ",")}/" +
                                Constants.TOTAL_BALANCE_KEYBOARD_TO_CALCULATOR
                    )
                })

        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshTotalBalance(
    viewModel: TotalBalanceViewModel,
    totalBalanceCurrenciesList: List<TotalBalanceCurrency>,
    lastSelectedIndex: Int,
    isRefreshing: Boolean,
    isChangeFocus: Boolean,
    lazyListState: LazyListState = rememberLazyListState(),
    textStateFromKeyboard: TextFieldValue,
    onRefresh: () -> Unit,
    onFocusChange: (Int, TotalBalanceCurrency, String) -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
    onChangeCurrency: (String, String, Boolean) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(LocalPaddingValues.current.small),

            ) {
            itemsIndexed(totalBalanceCurrenciesList) { index, currency ->
                // Check if this item was the last selected one
                val isFocused = index == lastSelectedIndex
                val textFieldValue = when {
                    isFocused && isChangeFocus -> {
                        TextFieldValue(
                            text = textStateFromKeyboard.text,
                            selection = TextRange(0, textStateFromKeyboard.text.length)
                        )
                    }

                    isFocused -> textStateFromKeyboard
                    else -> TextFieldValue(viewModel.formatDoubleToString(currency.currencyValue))
                }

                CurrencyCard(
                    currencyCode = currency.currencyCode,
                    currencyFlag = viewModel.getCurrencyFlag(currency.currencyCode),
                    isFocused = isFocused,
                    textFieldValue = textFieldValue,
                    onFocusChange = {
                        onFocusChange(index, currency, textFieldValue.text)
                    },
                    onTextChange = onTextChange,
                    onChangeCurrency = { code, value, isFocus ->
                        onChangeCurrency(code, value, isFocus)
                    }
                )
            }
        }

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
        )
    }
}
