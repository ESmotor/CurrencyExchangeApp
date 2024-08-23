package com.itskidan.currencyexchangeapp.ui.screens.addcurrency

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.application.App
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddCurrencyScreen(
    navController: NavHostController,
    viewModel: AddCurrencyViewModel = viewModel()
) {
    val screenWidthInDp = App.instance.screenWidthInDp
    val scope = rememberCoroutineScope()

    // create Data
    val activeCurrencyList by viewModel.activeCurrencyList.collectAsState(initial = emptyList())
    var selectedCurrenciesList by remember { mutableStateOf(listOf<String>()) }
    var otherCurrenciesList by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(activeCurrencyList) {
        selectedCurrenciesList = activeCurrencyList
        otherCurrenciesList = viewModel.getOtherCurrenciesList(activeCurrencyList)
    }

    // For Reordering
    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        selectedCurrenciesList = selectedCurrenciesList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    // Search system
    var searchText by remember { mutableStateOf("") }
    var searchingSelectedCurrenciesList by remember { mutableStateOf(listOf<String>()) }
    var searchingOtherCurrenciesList by remember { mutableStateOf(listOf<String>()) }
    searchingSelectedCurrenciesList = viewModel.filterBySearch(selectedCurrenciesList,searchText)
    searchingOtherCurrenciesList =viewModel.filterBySearch(otherCurrenciesList,searchText)

    LaunchedEffect(searchText) {
        lazyListState.scrollToItem(index = 0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.updateActiveCurrencyList(selectedCurrenciesList)
                        }
                        navController.popBackStack()
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
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(LocalPaddingValues.current.small),
            ) {
                // Active currency
                itemsIndexed(
                    searchingSelectedCurrenciesList,
                    key = { _, currencyCode -> currencyCode }) { index, currencyCode ->
                    ReorderableItem(reorderableLazyColumnState, currencyCode) {
                        val interactionSource = remember { MutableInteractionSource() }

                        Card(
                            shape = RectangleShape,
                            onClick = {
                                scope.launch {
                                    selectedCurrenciesList =
                                        selectedCurrenciesList.filterNot { it == currencyCode }
                                    otherCurrenciesList =
                                        otherCurrenciesList.toMutableList().apply {
                                            add(currencyCode)
                                        }.sorted()
                                }
                            },
                            modifier = Modifier
                                .height(80.dp)
                                .semantics {
                                    customActions = listOf(
                                        CustomAccessibilityAction(
                                            label = "Move Up",
                                            action = {
                                                if (index > 0) {
                                                    selectedCurrenciesList = selectedCurrenciesList
                                                        .toMutableList()
                                                        .apply {
                                                            add(index - 1, removeAt(index))
                                                        }
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                        CustomAccessibilityAction(
                                            label = "Move Down",
                                            action = {
                                                if (index < selectedCurrenciesList.size - 1) {
                                                    selectedCurrenciesList = selectedCurrenciesList
                                                        .toMutableList()
                                                        .apply {
                                                            add(index + 1, removeAt(index))
                                                        }
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                    )
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            interactionSource = interactionSource,
                        ) {
                            Row(
                                Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Checkbox
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Default.CheckBox,
                                        contentDescription = "CheckBox icon",
                                    )
                                }

                                // Flag
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width(80.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = viewModel.getCurrencyFlag(currencyCode)
                                        ),
                                        contentDescription = "Currency Flag Country",
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center,
                                    )
                                }

                                // Code and name of currency
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width((screenWidthInDp - 150).dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = currencyCode,
                                                textAlign = TextAlign.Start,
                                                style = MaterialTheme.typography.titleLarge

                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = viewModel.getCurrencyName(currencyCode),
                                                textAlign = TextAlign.Start,
                                                style = MaterialTheme.typography.titleMedium

                                            )
                                        }
                                    }


                                }
                                // Icon Button for draggable
                                IconButton(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(80.dp)
                                        .draggableHandle(
                                            onDragStarted = {
                                            },
                                            onDragStopped = {
                                            },
                                            interactionSource = interactionSource,
                                            enabled = searchText.isEmpty()
                                        )
                                        .clearAndSetSemantics { }
                                        .let {
                                            if (searchText.isNotEmpty()) {
                                                it.alpha(0.0f)
                                            } else {
                                                it
                                            }
                                        },
                                    onClick = {},
                                    enabled = searchText.isEmpty()
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

                //Divider between two lists
                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                    )
                }

                //Passive currency
                items(searchingOtherCurrenciesList) { currencyCode ->
                    Card(
                        shape = RectangleShape,
                        onClick = {
                            scope.launch {
                                selectedCurrenciesList = selectedCurrenciesList
                                    .toMutableList()
                                    .apply {
                                        add(currencyCode)
                                    }
                                otherCurrenciesList = otherCurrenciesList
                                    .filterNot { it == currencyCode }.sorted()
                            }
                        },
                        modifier = Modifier
                            .height(80.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = Color.Gray),
                                onClick = {}
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            // Checkbox
                            Box(
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(80.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    modifier = Modifier.fillMaxSize(),
                                    imageVector = Icons.Default.CheckBoxOutlineBlank,
                                    contentDescription = "CheckBox icon",
                                )
                            }

                            // Flag
                            Box(
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(80.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = viewModel.getCurrencyFlag(currencyCode)
                                    ),
                                    contentDescription = "Currency Flag Country",
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(50.dp),
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.Center,
                                )
                            }

                            // Code and name of currency
                            Box(
                                modifier = Modifier
                                    .height(80.dp)
                                    .width((screenWidthInDp - 150).dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = currencyCode,
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.titleLarge

                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = viewModel.getCurrencyName(currencyCode),
                                            textAlign = TextAlign.Start,
                                            style = MaterialTheme.typography.titleMedium

                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

