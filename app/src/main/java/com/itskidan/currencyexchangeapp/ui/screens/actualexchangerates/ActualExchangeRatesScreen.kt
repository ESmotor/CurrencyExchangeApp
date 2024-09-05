package com.itskidan.currencyexchangeapp.ui.screens.actualexchangerates

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
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.ui.components.CurrencyCard
import com.itskidan.currencyexchangeapp.ui.components.KeyboardForTyping
import com.itskidan.currencyexchangeapp.ui.components.UpdateBox
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import com.itskidan.currencyexchangeapp.utils.NavigationConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ActualExchangeRatesScreen(
    viewModel: ActualExchangeRatesViewModel = viewModel(),
    innerPadding: PaddingValues,
    navController: NavHostController,
    scope: CoroutineScope,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    val isChangeFocus = remember { mutableStateOf(false) }
    val textStateFromKeyboard = remember { mutableStateOf(TextFieldValue("")) }

    //Checking for last update time
    val lastUpdateTimeRates by viewModel.lastUpdateTimeRates.collectAsState(initial = 0L)
    var updateTime by remember { mutableStateOf("") }

    LaunchedEffect(lastUpdateTimeRates) {
        if (lastUpdateTimeRates > 0L) {
            updateTime = TimeUtils.getFormattedCurrentTime(lastUpdateTimeRates)
        }
    }

    // Initialize state
    var lastSelectedIndex by remember { mutableIntStateOf(-1) }
    var lastSelectedValue by remember { mutableStateOf("0") }

    LaunchedEffect(Unit) {
        val (index, code, value) = viewModel.getLastSelectedState()
        lastSelectedIndex = index
        lastSelectedValue = value
        textStateFromKeyboard.value =
            TextFieldValue(lastSelectedValue, TextRange(lastSelectedValue.length))
        viewModel.updateCurrentInput(code, value)
        viewModel.updateActiveCurrencyRates()
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
                .weight(58f)
                .fillMaxSize()
                .zIndex(0f)
        ) {
            PullToRefreshLazyColumn(
                viewModel = viewModel,
                lastSelectedIndex = lastSelectedIndex,
                isRefreshing = isRefreshing,
                isChangeFocus = isChangeFocus.value,
                textStateFromKeyboard = textStateFromKeyboard.value,
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
                onFocusChange = { index, code, value ->
                    lastSelectedIndex = index
                    lastSelectedValue = value
                    viewModel.updateCurrentInput(code, value)
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
                    val currencyCode = code.ifEmpty { "USD" }
                    val currencyValue = value.ifEmpty { "0" }
                    navController.navigate(
                        "${NavigationConstants.CHANGE_CURRENCY}/" +
                                "$currencyCode/" +
                                "$currencyValue/" +
                                "$isFocused/" +
                                Constants.ACTUAL_RATES_LIST_TO_CHANGE_CURRENCY
                    )
                }
            )

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
                    isChangeFocus.value = false
                    textStateFromKeyboard.value = newTextState
                },
                onFocusChange = { value ->
                    if (value) isChangeFocus.value = false
                },
                onAddCurrencies = {
                    navController.navigate(
                        "${NavigationConstants.ADD_CURRENCY}/" +
                                Constants.ACTUAL_RATES_KEYBOARD_TO_ADD_CURRENCY
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
                    val (code, rate) = viewModel.getCurrentInput()
                    navController.navigate(
                        "${NavigationConstants.CALCULATOR}/" +
                                "$code/" +
                                "${rate.replace(".", ",")}/" +
                                Constants.ACTUAL_RATES_KEYBOARD_TO_CALCULATOR
                    )
                })

        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    viewModel: ActualExchangeRatesViewModel,
    lastSelectedIndex: Int,
    isRefreshing: Boolean,
    isChangeFocus: Boolean,
    lazyListState: LazyListState = rememberLazyListState(),
    textStateFromKeyboard: TextFieldValue,
    onRefresh: () -> Unit,
    onFocusChange: (Int, String, String) -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
    onChangeCurrency: (String, String, Boolean) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val calculatedRates by viewModel.activeCurrencyRates.collectAsState(emptyMap())
    val activeCurrencyList by viewModel.activeCurrencyCodeList.collectAsState(initial = emptyList())
    val ratesFromDatabase by viewModel.ratesFromDatabase.collectAsState(initial = emptyMap())

    LaunchedEffect(activeCurrencyList, ratesFromDatabase) {
        if (activeCurrencyList.isNotEmpty() && ratesFromDatabase.isNotEmpty()) {
            viewModel.updateActiveCurrencyRates()
        }
    }

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
            itemsIndexed(activeCurrencyList) { index, currencyCode ->
                // Check if this item was the last selected one
                val isInitiallyFocused = index == lastSelectedIndex
                val rate = calculatedRates[currencyCode] ?: "0"
                val textFieldValue = when {
                    isInitiallyFocused && isChangeFocus -> {
                        TextFieldValue(
                            text = textStateFromKeyboard.text,
                            selection = TextRange(0, textStateFromKeyboard.text.length)
                        )
                    }

                    isInitiallyFocused -> textStateFromKeyboard
                    else -> TextFieldValue(rate)
                }

                LaunchedEffect(textStateFromKeyboard) {
                    if (isInitiallyFocused) {
                        val value = textStateFromKeyboard.text
                        viewModel.updateCurrentInput(currencyCode, value)
                        viewModel.saveSelectedLastState(currencyCode, value)
                        if (!isChangeFocus) {
                            viewModel.updateActiveCurrencyRates()
                        }
                    }
                }

                CurrencyCard(
                    currencyCode = currencyCode,
                    currencyFlag = viewModel.getCurrencyFlag(currencyCode),
                    isFocused = isInitiallyFocused,
                    textFieldValue = textFieldValue,
                    onFocusChange = { onFocusChange(index, currencyCode, textFieldValue.text) },
                    onTextChange = onTextChange,
                    onChangeCurrency = { code,value,isFocused->
                        onChangeCurrency(code, value, isFocused)
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