package com.itskidan.currencyexchangeapp.ui.screens.addcurrency

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.components.AdBannerView
import com.itskidan.currencyexchangeapp.ui.components.CurrencyCodeAndName
import com.itskidan.currencyexchangeapp.ui.components.CurrencyFlag
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddCurrencyScreen(
    navController: NavHostController,
    viewModel: AddCurrencyViewModel = viewModel(),
    locationOfRequest: String,
) {
    Timber.tag("MyLog").d("locationOfRequest:$locationOfRequest")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isEnableBackBtn by remember { mutableStateOf(true) }

    viewModel.loadInterstitialAd(context)

    // create Data
    var selectedList by remember { mutableStateOf(listOf<String>()) }
    var otherList by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        selectedList = viewModel.getSelectedCurrencyList(locationOfRequest)
        otherList = viewModel.getOtherCurrenciesList(selectedList)
    }

    // For Reordering
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        selectedList = selectedList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    // Search system
    var searchText by remember { mutableStateOf("") }
    var searchingSelectedList by remember { mutableStateOf(listOf<String>()) }
    var searchingOtherList by remember { mutableStateOf(listOf<String>()) }
    searchingSelectedList = viewModel.filterBySearch(selectedList, searchText)
    searchingOtherList = viewModel.filterBySearch(otherList, searchText)

    LaunchedEffect(searchText) {
        lazyListState.scrollToItem(index = 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.updateActiveCurrencyList(selectedList, locationOfRequest)

                            viewModel.showInterstitialAd(context) {}

                            if (isEnableBackBtn) {
                                isEnableBackBtn = false
                                navController.popBackStack()

                                scope.launch {
                                    delay(700)
                                    isEnableBackBtn = true
                                }
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            "Back"
                        )
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
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
                .imePadding()
                .consumeWindowInsets(innerPadding)
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(LocalPaddingValues.current.small),
                ) {
                    // Active currency

                    itemsIndexed(searchingSelectedList, key = { _, currencyCode -> currencyCode })
                    { index, currencyCode ->
                        ReorderableItem(reorderableLazyColumnState, currencyCode) {
                            val interactionSource = remember { MutableInteractionSource() }
                            AddCurrencyCard(
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = {},
                                    onDragStopped = {},
                                    interactionSource = interactionSource,
                                    enabled = searchText.isEmpty()
                                ),
                                isDraggable = searchText.isEmpty(),
                                isSelected = true,
                                currencyCode = currencyCode,
                                currencyName = viewModel.getCurrencyName(currencyCode),
                                currencyFlag = viewModel.getCurrencyFlag(currencyCode),
                                interactionSource = interactionSource,
                                onClick = {
                                    when (locationOfRequest) {
                                        Constants.ACTUAL_RATES_KEYBOARD_TO_ADD_CURRENCY -> {
                                            if (selectedList.size > 2) {
                                                selectedList = selectedList.filterNot { it == currencyCode }
                                                otherList = otherList.toMutableList()
                                                    .apply { add(currencyCode) }
                                                    .sorted()
                                            } else {
                                                val toastText =
                                                    context.getString(R.string.add_currency_screen_minimum_number_of_currencies)
                                                Toast.makeText(context, toastText, Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                        Constants.TOTAL_BALANCE_KEYBOARD_TO_ADD_CURRENCY -> {

                                        }
                                    }

                                },
                                actionMoveUp = {
                                    if (index > 0) {
                                        selectedList = selectedList.toMutableList()
                                            .apply { add(index - 1, removeAt(index)) }
                                        true
                                    } else {
                                        false
                                    }
                                },
                                actionMoveDown = {
                                    if (index < selectedList.size - 1) {
                                        selectedList = selectedList.toMutableList()
                                            .apply { add(index + 1, removeAt(index)) }
                                        true
                                    } else {
                                        false
                                    }
                                }
                            )
                        }
                    }

                    //Divider between two lists
                    item {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                        )
                    }

                    //Passive currency
                    items(searchingOtherList) { currencyCode ->
                        AddCurrencyCard(
                            isSelected = false,
                            currencyCode = currencyCode,
                            currencyName = viewModel.getCurrencyName(currencyCode),
                            currencyFlag = viewModel.getCurrencyFlag(currencyCode),
                            onClick = {
                                selectedList =
                                    selectedList.toMutableList().apply { add(currencyCode) }
                                otherList = otherList.filterNot { it == currencyCode }.sorted()
                            },
                        )
                    }
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
fun AddCurrencyCard(
    modifier: Modifier = Modifier,
    isDraggable: Boolean = false,
    isSelected: Boolean,
    currencyCode: String,
    currencyName: String,
    currencyFlag: Int,
    onClick: () -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    actionMoveUp: () -> Boolean = { false },
    actionMoveDown: () -> Boolean = { false },
) {
    Card(
        shape = RectangleShape,
        onClick = onClick,
        modifier = Modifier
            .height(70.dp)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction("Move Up", actionMoveUp),
                    CustomAccessibilityAction("Move Down", actionMoveDown),
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        interactionSource = interactionSource,
    ) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(LocalPaddingValues.current.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Checkbox
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = "CheckBox icon",
                modifier = Modifier.size(30.dp)
            )

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

            // Drag Handle Icon Button
            if (isDraggable && isSelected) {
                IconButton(
                    onClick = {},
                    modifier = modifier
                        .size(30.dp)
                        .clearAndSetSemantics {}
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_drag_handle_24),
                        contentDescription = "Reorder"
                    )
                }
            }
        }
    }
}
