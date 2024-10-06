package com.itskidan.currencyexchangeapp.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.components.AdBannerView
import com.itskidan.currencyexchangeapp.ui.components.SettingsCard
import com.itskidan.currencyexchangeapp.utils.NavigationConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel(),
    locationOfRequest: String,
) {
    Timber.tag("MyLog").d("locationOfRequest:$locationOfRequest")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isEnableBackBtn by remember { mutableStateOf(true) }

    val actualRatesCurrencies by viewModel.actualRatesCodeList.collectAsState(emptyList())
    val totalBalanceCurrencies by viewModel.totalBalanceCodeList.collectAsState(emptyList())
    val selectedCurrency by viewModel.totalBalanceSelectedCurrency.collectAsState("")

    var items by remember { mutableStateOf<List<Triple<String, String, String>>>(emptyList()) }

    LaunchedEffect(actualRatesCurrencies, totalBalanceCurrencies, selectedCurrency) {
        if (actualRatesCurrencies.isNotEmpty() && selectedCurrency.isNotEmpty()) {
            val totalBalanceValue = if(totalBalanceCurrencies.isEmpty())
                "0 - " + context.getString(R.string.settings_screen_no_one_currency_selected)
                else
                "${totalBalanceCurrencies.count()} - ${totalBalanceCurrencies.joinToString(", ")}"

            items = listOf(
                Triple(
                    context.getString(R.string.settings_screen_actual_rates_currencies),
                    "${actualRatesCurrencies.count()} - ${actualRatesCurrencies.joinToString(", ")}",
                    Constants.ACTUAL_RATES_KEYBOARD_TO_ADD_CURRENCY
                ),
                Triple(
                    context.getString(R.string.settings_screen_total_balance_currencies),
                    totalBalanceValue,
                    Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY
                ),
                Triple(
                    context.getString(R.string.settings_screen_home_currency_for_total_balance),
                    selectedCurrency,
                    Constants.TOTAL_BALANCE_SELECTED_TO_CHANGE_CURRENCY
                ),
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings_screen_top_app_bar_title))
                },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEnableBackBtn) {
                            isEnableBackBtn = false
                            navController.popBackStack()

                            scope.launch {
                                delay(1000)
                                isEnableBackBtn = true
                            }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                },
            )
        },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (items.isNotEmpty()) {
                    SettingScreenContent(
                        itemList = items,
                        onCardClick = { _, subtitle, locationOfRequest ->
                            Timber.tag("MyLog").d("locationOfRequest: $locationOfRequest")
                            when (locationOfRequest) {
                                Constants.ACTUAL_RATES_KEYBOARD_TO_ADD_CURRENCY -> {
                                    navController.navigate(
                                        "${NavigationConstants.ADD_CURRENCY}/" +
                                                locationOfRequest
                                    )
                                }

                                Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY -> {
                                    navController.navigate(
                                        "${NavigationConstants.ADD_CURRENCY}/" +
                                                Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY
                                    )
                                }

                                Constants.TOTAL_BALANCE_SELECTED_TO_CHANGE_CURRENCY -> {
                                    val value = "0.0"
                                    val isFocused = false
                                    navController.navigate(
                                        "${NavigationConstants.CHANGE_CURRENCY}/" +
                                                "$subtitle/" +
                                                "$value/" +
                                                "$isFocused/" +
                                                Constants.TOTAL_BALANCE_SELECTED_TO_CHANGE_CURRENCY
                                    )
                                }
                            }
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                AdBannerView(modifier = Modifier.height(50.dp))
            }
        }






    }
}

@Composable
fun SettingScreenContent(
    modifier: Modifier = Modifier,
    itemList: List<Triple<String, String, String>>,
    onCardClick: (String, String, String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(itemList) { item ->
            SettingsCard(
                title = item.first,
                subtitle = item.second,
                onCardClick = { onCardClick(item.first, item.second, item.third) }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp,
            )
        }
    }
}