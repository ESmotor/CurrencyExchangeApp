package com.itskidan.currencyexchangeapp.ui.screens.changecurreny

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.ui.components.CurrencyCodeAndName
import com.itskidan.currencyexchangeapp.ui.components.CurrencyFlag
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCurrencyScreen(
    navController: NavHostController,
    viewModel: ChangeCurrencyViewModel = viewModel(),
    isFocused: Boolean,
    oldCurrencyCode: String,
    oldCurrencyValue: String,
    locationOfRequest: String,
) {
    Timber.tag("MyLog").d("fromScreen:$locationOfRequest")
    val scope = rememberCoroutineScope()

    var currenciesList by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        currenciesList = viewModel.reorderCurrencyList(oldCurrencyCode)
    }

    // For Reordering
    val lazyListState = rememberLazyListState()
    // Search system
    var searchText by remember { mutableStateOf("") }
    var searchingCurrenciesList by remember { mutableStateOf(listOf<String>()) }
    searchingCurrenciesList = viewModel.filterBySearch(currenciesList, searchText)

    LaunchedEffect(searchText) {
        lazyListState.scrollToItem(index = 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                },
                modifier = Modifier.statusBarsPadding(),
                title = {
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge + TextStyle(MaterialTheme.colorScheme.outline),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.outline),
                        maxLines = 1,
                        decorationBox = { innerTextField ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(LocalPaddingValues.current.small)
                            ) {
                                if (searchText.isEmpty()) {
                                    Text("Search...", color = MaterialTheme.colorScheme.outline)
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = lazyListState,
        ) {

            // Passive currency
            items(searchingCurrenciesList) { newCurrencyCode ->
                ChangeCurrencyCard(
                    currencyCode = newCurrencyCode,
                    currencyFlag = viewModel.getCurrencyFlag(newCurrencyCode),
                    currencyName = viewModel.getCurrencyName(newCurrencyCode),
                    onCardClick = {
                        scope.launch {
                            viewModel.onCurrencyClick(
                                isFocused = isFocused,
                                oldCurrencyCode = oldCurrencyCode,
                                newCurrencyCode = newCurrencyCode,
                                oldCurrencyValue = oldCurrencyValue,
                                locationOfRequest = locationOfRequest
                            )
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun ChangeCurrencyCard(
    currencyCode: String,
    currencyFlag: Int,
    currencyName: String,
    onCardClick: () -> Unit
) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .height(70.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.Gray),
                onClick = onCardClick
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(LocalPaddingValues.current.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag
            CurrencyFlag(
                currencyFlag = currencyFlag,
                modifier = Modifier.align(Alignment.CenterVertically),
                size = 50.dp,
                borderSize = 2.dp,
                borderColor = MaterialTheme.colorScheme.primaryContainer,
            )

            // Code and name of currency
            CurrencyCodeAndName(
                modifier = Modifier.align(Alignment.CenterVertically),
                currencyCode = currencyCode,
                currencyCodeStyle = MaterialTheme.typography.titleLarge,
                currencyCodeColor = MaterialTheme.colorScheme.onSurface,
                currencyName = currencyName,
                currencyNameStyle = MaterialTheme.typography.titleMedium,
                currencyNameColor = MaterialTheme.colorScheme.onSurface
            )
            // Spacer to push the button to the right
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}