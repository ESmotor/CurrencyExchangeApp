package com.itskidan.currencyexchangeapp.ui.home

import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyExchange
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import com.itskidan.currencyexchangeapp.utils.Constants
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController,viewModel: HomeScreenViewModel = viewModel()) {
    val context = LocalContext.current
    //create ScreenSize
    val screenWidthInDp = getScreenWidthInDp(context)
    val screenHeightInDp = getScreenHeightInDp(context)
    //create Data
    val currencyList by viewModel.databaseFromDB.collectAsState(initial = emptyList())
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
// icons to mimic drawer destinations
    val items =
        listOf(
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
                    navController.navigate(Constants.ADD_CURRENCY)}) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                ) {
                    items(currencyList) {
                        CurrencyListItemForHomeScreen(
                            currency = it,
                            screenWidthInDp = screenWidthInDp,
                            screenHeightInDp = screenHeightInDp
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun CurrencyListItemForHomeScreen(
    currency: Currency,
    modifier: Modifier = Modifier,
    screenWidthInDp: Int,
    screenHeightInDp: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val focusRequester = remember { FocusRequester() }
    val isFocusedTextField = remember { mutableStateOf(false) }
    val isFocusedCalculator = remember { mutableStateOf(false) }
    // currency settings
    val currencyFlagResourceId = CurrencyUtils.currencyFlagMap[currency.currencyCode]?:0
    // current screen size in dp
    // height settings
    val minBoxHeight = 80
    val boxHeight =
        if (screenHeightInDp / 11 <= minBoxHeight) minBoxHeight else screenHeightInDp / 11
    // width settings
    val firstBoxWidth = screenWidthInDp / 5
    val secondBoxWidth = screenWidthInDp / 7
    val thirdBoxWidth = screenWidthInDp / 14
    val fifthBoxWidth = (screenWidthInDp / 7.5).toInt()
    val fourthBoxWidth =
        screenWidthInDp - firstBoxWidth - secondBoxWidth - thirdBoxWidth - fifthBoxWidth

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (isFocusedTextField.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
    ) {
        // This is flag
        Box(
            modifier = Modifier
                .height(boxHeight.dp)
                .width(boxHeight.dp),
            contentAlignment = Alignment.Center,
        ) {
//            Image(
//                painter = painterResource(id = currencyFlagResourceId),
//                contentDescription = "Currency Flag Country",
//                modifier = Modifier
//                    .width((boxHeight / 4 * 2).dp)
//                    .height((boxHeight / 4 * 2).dp),
//                contentScale = ContentScale.Crop,
//                alignment = Alignment.Center,
//            )
            Image(
                imageVector = ImageVector.vectorResource(currencyFlagResourceId),
                contentDescription = "Currency Flag Country",
                modifier = Modifier
                    .width((boxHeight / 8 * 5).dp)
                    .height((boxHeight / 8 * 5).dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
        }
        // This is name of currency
        Box(
            modifier = Modifier
                .height(boxHeight.dp)
                .width(secondBoxWidth.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = currency.currencyCode,
                textAlign = TextAlign.Center,
                color = if (isFocusedTextField.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge

            )
        }
        // This is icon for change currency
        Box(
            modifier = Modifier
                .width(thirdBoxWidth.dp)
                .height(boxHeight.dp),
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
                .width(fourthBoxWidth.dp)
                .height(boxHeight.dp),

            contentAlignment = Alignment.Center,
        ) {
            TextField(
                value = textState.value,
                onValueChange = { newValue ->
                    // We limit the input rules (numbers and one comma)
                    if (newValue.text.all {
                            "0123456789,.".contains(it)
                        }) {
                        var newText = newValue.text.replace(".", ",")
                        if (newText.count { it == ',' } <= 1) {
                            if (newText.indexOf(',') == 0) {
                                newText = "0$newText"
                            }
                            if (newText.length >= 2 && !newText.contains(',') && newText.startsWith(
                                    '0'
                                )
                            ) {
                                newText = newText.drop(1)
                            }
                            textState.value =
                                TextFieldValue(newText, selection = TextRange(newText.length))
                        }
                    }
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End) + MaterialTheme.typography.titleLarge,
                placeholder = { Text(stringResource(R.string.home_screen_text_field_rate_placeholder)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .shadow(
                        elevation = LocalPaddingValues.current.extraSmall,
                        shape = MaterialTheme.shapes.small
                    )
                    .fillMaxWidth()
                    .height((boxHeight * 0.7).toInt().dp)
                    .heightIn(min = 56.dp)
                    .onFocusChanged { focusState ->
                        isFocusedTextField.value = focusState.isFocused
                    }
                    .focusRequester(focusRequester),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
//                    focusedContainerColor = Color.White,
//                    unfocusedContainerColor = Color.White,
//                    disabledContainerColor = Color.White,
//                    focusedTextColor = Color.Black,
//                    unfocusedTextColor = Color.Black,
//                    disabledTextColor = Color.Black,
//                    cursorColor = Color.Blue,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
                ),
            )
        }
        // This is icon for calculator
        Box(
            modifier = Modifier
                .width(fifthBoxWidth.dp)
                .height(boxHeight.dp)
                .clickable(
                    onClick = {
                        scope.launch {
                            isFocusedCalculator.value = !isFocusedCalculator.value
                            Toast
                                .makeText(context, "Calculate icon clicked", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(fifthBoxWidth.dp)
                    .width(fifthBoxWidth.dp),
                tint = if (isFocusedTextField.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                imageVector = Icons.Default.Calculate,
                contentDescription = stringResource(R.string.home_screen_calculate_icon_description),
            )
        }
    }
}

fun getScreenWidthInDp(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val result = (displayMetrics.widthPixels / displayMetrics.density).toInt()
    Timber.tag("MyLog").d("width = %s", result)
    return result
}

fun getScreenHeightInDp(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val result = (displayMetrics.heightPixels / displayMetrics.density).toInt()
    Timber.tag("MyLog").d("height = %s", result)
    return result
}