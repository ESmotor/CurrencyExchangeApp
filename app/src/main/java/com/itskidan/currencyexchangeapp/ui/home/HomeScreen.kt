@file:OptIn(ExperimentalMaterial3Api::class)

package com.itskidan.currencyexchangeapp.ui.home

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import com.itskidan.currencyexchangeapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Set a flag to prevent sleep mode
    KeepScreenOn(context)
    var isRefreshing by remember { mutableStateOf(false) }
    var isUpdating by remember { mutableStateOf(false) }
    val isChangeFocus = remember { mutableStateOf(false) }
    val textStateFromKeyboard = remember { mutableStateOf(TextFieldValue("")) }

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
    // Drawer menu
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    // icons to mimic drawer destinations
    val items = viewModel.getIconsForDrawerMenu()
    val selectedItem = remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(item.name.substringAfterLast(".")) },
                            selected = item == selectedItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.home_screen_top_app_bar_title))
                    },
                    modifier = Modifier.statusBarsPadding(),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.home_screen_navigation_menu_description)
                            )
                        }
                    },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.home_screen_vertical_overflow_menu_description)
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.home_screen_overflow_menu_actual_exchange_rate_title)) },
                                onClick = {
                                    scope.launch {
                                        Toast.makeText(
                                            context,
                                            "Actual Exchange Rate",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.CurrencyExchange,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.home_screen_overflow_menu_private_exchange_rate_title)) },
                                onClick = {
                                    scope.launch {
                                        Toast.makeText(
                                            context,
                                            "Private Exchange Rate",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.home_screen_overflow_menu_world_time_title)) },
                                onClick = {
                                    scope.launch {
                                        Toast.makeText(
                                            context,
                                            "World Time",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
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
                ) {
                    UpdateFieldForHomeScreen(
                        viewModel = viewModel,
                        isUpdating = isUpdating
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(58f)
                        .fillMaxSize()
                        .zIndex(0f)
                ) {
                    PullToRefreshLazyColumn(
                        scope = scope,
                        viewModel = viewModel,
                        lastSelectedIndex = lastSelectedIndex,
                        isRefreshing = isRefreshing,
                        isChangeFocus = isChangeFocus.value,
                        textStateFromKeyboard = textStateFromKeyboard.value,
                        navController = navController,
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
                        onFocusChange = { index, _, value ->
                            lastSelectedIndex = index
                            lastSelectedValue = value
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
                        scope = scope,
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
                            navController.navigate(Constants.ADD_CURRENCY)
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
                                "${Constants.CALCULATOR}/$code/${rate.replace(".", ",")}"
                            )
                        })

                }
                Box(
                    modifier = Modifier
                        .weight(10f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Advertising Space",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    scope: CoroutineScope,
    viewModel: HomeScreenViewModel,
    navController: NavHostController,
    lastSelectedIndex: Int,
    isRefreshing: Boolean,
    isChangeFocus: Boolean,
    lazyListState: LazyListState = rememberLazyListState(),
    textStateFromKeyboard: TextFieldValue,
    onRefresh: () -> Unit,
    onFocusChange: (Int, String, String) -> Unit,
    onTextChange: (TextFieldValue) -> Unit
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
                if (calculatedRates.isNotEmpty()
                    && activeCurrencyList.isNotEmpty()
                    && ratesFromDatabase.isNotEmpty()
                ) {
                    val isLastSelected = index == lastSelectedIndex
                    CurrencyListItem(
                        viewModel = viewModel,
                        scope = scope,
                        navController = navController,
                        index = index,
                        isChangeFocus = isChangeFocus,
                        currencyCode = currencyCode,
                        rate = calculatedRates[currencyCode] ?: "0",
                        isInitiallyFocused = isLastSelected,
                        textStateFromKeyboard = textStateFromKeyboard,
                        onFocusChange = onFocusChange,
                        onTextChange = onTextChange
                    )
                }
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


@Composable
fun CurrencyListItem(
    viewModel: HomeScreenViewModel = viewModel(),
    scope: CoroutineScope,
    navController: NavHostController,
    index: Int,
    isChangeFocus: Boolean,
    currencyCode: String,
    rate: String,
    isInitiallyFocused: Boolean,
    textStateFromKeyboard: TextFieldValue,
    onFocusChange: (Int, String, String) -> Unit,
    onTextChange: (TextFieldValue) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val itemHeight = 60.dp

    LaunchedEffect(isInitiallyFocused) {
        if (isInitiallyFocused) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(textStateFromKeyboard) {
        if (isInitiallyFocused) {
            val value = textStateFromKeyboard.text
            viewModel.updateCurrentInput(currencyCode, value)
            viewModel.saveSelectedLastState(currencyCode, textStateFromKeyboard.text)
            if (!isChangeFocus) {
                viewModel.updateActiveCurrencyRates()
            }
        }
    }

    Card(
        shape = RectangleShape,
        onClick = {
            scope.launch { }
        },
        modifier = Modifier
            .height(itemHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    if (isInitiallyFocused) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            //  Flag and Name of currency and change Icon
            Box(
                modifier = Modifier
                    .weight(37f)
                    .clickable(
                        onClick = {
                            navController.navigate("${Constants.CHANGE_CURRENCY}/$currencyCode/$rate/$isInitiallyFocused")
                        }
                    )
                    .padding(start = LocalPaddingValues.current.extraSmall),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .weight(35f)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primaryContainer,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(
                                viewModel.getCurrencyFlag(currencyCode)
                            ),
                            contentDescription = "Currency Flag Country",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(45f)
                    ) {
                        Text(
                            text = currencyCode,
                            textAlign = TextAlign.Center,
                            color = if (isInitiallyFocused) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge

                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(20f)

                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize(),
                            imageVector = Icons.Default.ArrowDropDown,
                            tint = if (isInitiallyFocused) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Triangle list icon",
                        )
                    }
                }


            }
            // This is edit text for write amount
            CompositionLocalProvider(
                LocalTextInputService provides null
            ) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = LocalPaddingValues.current.extraSmall,
                            shape = MaterialTheme.shapes.small
                        )
                        .clip(RoundedCornerShape(LocalPaddingValues.current.small))

                        .background(MaterialTheme.colorScheme.surface)
                        .weight(55f)

                        .padding(
                            horizontal = LocalPaddingValues.current.extraSmall,
                            vertical = LocalPaddingValues.current.small
                        ),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    BasicTextField(
                        value = if (isInitiallyFocused) {
                            if (isChangeFocus) {
                                val value = textStateFromKeyboard.text
                                TextFieldValue(
                                    text = value,
                                    selection = TextRange(start = 0, end = value.length)
                                )
                            } else {
                                textStateFromKeyboard
                            }


                        } else {
                            TextFieldValue(rate)
                        },
                        onValueChange = onTextChange,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.End,
                        ) + MaterialTheme.typography.titleLarge,
                        singleLine = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    onFocusChange(index, currencyCode, rate)
                                }
                            }
                            .focusRequester(focusRequester),
                    )
                }
            }
            // This is icon for calculator
            Box(
                modifier = Modifier
                    .weight(8f)
                    .padding(horizontal = LocalPaddingValues.current.extraSmall)
                    .clickable(
                        onClick = {
//                            scope.launch {
//                                viewModel.saveSelectedLastState(
//                                    code = currencyCode,
//                                    value = textStateFromKeyboard.text
//                                )
//                                focusRequester.requestFocus()
//                                isFocusedCalculator.value =
//                                    !isFocusedCalculator.value
//                            }
                        }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),

                    tint = if (isInitiallyFocused) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                    imageVector = Icons.Default.Calculate,
                    contentDescription = stringResource(R.string.home_screen_calculate_icon_description),
                )
            }
        }
    }
}

@Composable
fun UpdateFieldForHomeScreen(
    viewModel: HomeScreenViewModel,
    isUpdating: Boolean,
) {
    val lastUpdateTimeRates by viewModel.lastUpdateTimeRates.collectAsState(initial = 0L)
    var updateTime by remember { mutableStateOf("") }

    LaunchedEffect(lastUpdateTimeRates) {
        if (lastUpdateTimeRates > 0L) {
            updateTime = viewModel.getFormattedCurrentTime(lastUpdateTimeRates)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {

        if (isUpdating) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            if (updateTime.isNotEmpty()) {
                Text(
                    text = "Updated: $updateTime",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}


@Composable
fun KeepScreenOn(context: Context) {
    SideEffect {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

