@file:OptIn(ExperimentalMaterial3Api::class)

package com.itskidan.currencyexchangeapp.ui.home

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.application.App
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

    //create ScreenSize
    val screenWidthInDp = App.instance.screenWidthInDp

    var isRefreshing by remember { mutableStateOf(false) }


    // Initialize state
    var lastSelectedIndex by remember { mutableIntStateOf(-1) }
    var lastSelectedValue by remember { mutableStateOf("0") }

    //create Data
    val activeCurrencyList by viewModel.activeCurrencyList.collectAsState(initial = emptyList())

    LaunchedEffect(activeCurrencyList) {
        if (activeCurrencyList.isNotEmpty()) {
            val (index, value) = viewModel.getSelectedPositionAndValue()
            lastSelectedIndex = index
            lastSelectedValue = value
        }
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
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate(Constants.ADD_CURRENCY)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            PullToRefreshLazyColumn(
                innerPadding = innerPadding,
                scope = scope,
                viewModel = viewModel,
                activeCurrencyList = activeCurrencyList,
                lastSelectedIndex = lastSelectedIndex,
                lastSelectedValue = lastSelectedValue,
                screenWidthInDp = screenWidthInDp,
                isRefreshing = isRefreshing,
                onRefresh = {
                    scope.launch {
                        isRefreshing = true
                        viewModel.updateDatabaseRates()
                        delay(500)
                        isRefreshing = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    viewModel: HomeScreenViewModel,
    activeCurrencyList: List<String>,
    lastSelectedIndex: Int,
    lastSelectedValue: String,
    screenWidthInDp: Int,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
) {


    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(LocalPaddingValues.current.small),

            ) {
            item {
                UpdateFieldForHomeScreen(
                    viewModel = viewModel
                )
            }
            itemsIndexed(activeCurrencyList) { index, currencyCode ->
                // Check if this item was the last selected one
                val isLastSelected = index == lastSelectedIndex
                CurrencyListItemForHomeScreen(
                    currencyCode = currencyCode,
                    lastSelectedValue = lastSelectedValue,
                    index = index,
                    screenWidthInDp = screenWidthInDp,
                    viewModel = viewModel,
                    scope = scope,
                    isInitiallyFocused = isLastSelected
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


@Composable
fun CurrencyListItemForHomeScreen(
    currencyCode: String,
    lastSelectedValue: String,
    index: Int,
    screenWidthInDp: Int,
    viewModel: HomeScreenViewModel = viewModel(),
    scope: CoroutineScope,
    isInitiallyFocused: Boolean
) {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    val isFocusedTextField = remember { mutableStateOf(false) }
    val isFocusedCalculator = remember { mutableStateOf(false) }

    val currentInput by viewModel.currentInput.collectAsState()
    val ratesFromDatabase by viewModel.ratesFromDatabase.collectAsState(emptyMap())

    LaunchedEffect(ratesFromDatabase, currentInput, isFocusedTextField) {
        if (ratesFromDatabase.isNotEmpty() || !isFocusedTextField.value) {
            val text = viewModel.getCalculatedRate(currencyCode, currentInput)
            textState.value = TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        }
    }

    LaunchedEffect(isInitiallyFocused) {
        if (isInitiallyFocused) {
            focusRequester.requestFocus()
            textState.value = TextFieldValue(
                text = lastSelectedValue,
                selection = TextRange(lastSelectedValue.length)
            )
        }
    }

    Card(
        shape = RectangleShape,
        onClick = {
            scope.launch { }
        },
        modifier = Modifier
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(if (isFocusedTextField.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // This is flag
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(
                        viewModel.getCurrencyFlag(currencyCode)
                    ),
                    contentDescription = "Currency Flag Country",
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                )
            }

            // Name of currency
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = currencyCode,
                    textAlign = TextAlign.Center,
                    color = if (isFocusedTextField.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge

                )
            }

            // This is icon for change currency
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(30.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.ArrowDropDown,
                    tint = if (isFocusedTextField.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    contentDescription = "Triangle list icon",
                )
            }

            // This is edit text for write amount
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width((screenWidthInDp - 230).dp),

                contentAlignment = Alignment.Center,
            ) {
                TextField(
                    value = textState.value,
                    onValueChange = { newValue ->
                        // We limit the input rules (numbers and one comma)
                        val validatedValue = viewModel.validateAndFormatInput(newValue.text)
                        textState.value = TextFieldValue(
                            validatedValue,
                            selection = TextRange(validatedValue.length)
                        )
                        viewModel.updateCurrentInput(currencyCode, validatedValue)
                        viewModel.saveSelectedPositionAndValue(
                            position = index,
                            value = validatedValue
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End) + MaterialTheme.typography.titleLarge,
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .shadow(
                            elevation = LocalPaddingValues.current.extraSmall,
                            shape = MaterialTheme.shapes.small
                        )
                        .fillMaxWidth()
                        .height(60.dp)
                        .heightIn(min = 56.dp)
                        .onFocusChanged { focusState ->
                            isFocusedTextField.value = focusState.isFocused
                            viewModel.updateCurrentInput(currencyCode, textState.value.text)
                        }
                        .focusRequester(focusRequester),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                )
            }
            // This is icon for calculator
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(55.dp)
                    .clickable(
                        onClick = {
                            scope.launch {
                                viewModel.saveSelectedPositionAndValue(
                                    position = index,
                                    value = textState.value.text
                                )
                                focusRequester.requestFocus()
                                isFocusedCalculator.value =
                                    !isFocusedCalculator.value
                            }
                        }),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(55.dp)
                        .width(55.dp),
                    tint = if (isFocusedTextField.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    imageVector = Icons.Default.Calculate,
                    contentDescription = stringResource(R.string.home_screen_calculate_icon_description),
                )
            }
        }
    }
}

@Composable
fun UpdateFieldForHomeScreen(
    viewModel: HomeScreenViewModel
) {
    val lastUpdateTimeRates by viewModel.lastUpdateTimeRates.collectAsState(initial = 0L)
    var updateTime by remember { mutableStateOf("") }
    LaunchedEffect(lastUpdateTimeRates) {
        if (lastUpdateTimeRates > 0L) {
            updateTime = viewModel.getFormattedCurrentTime(lastUpdateTimeRates)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.Absolute.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (updateTime.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Updated: $updateTime",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
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