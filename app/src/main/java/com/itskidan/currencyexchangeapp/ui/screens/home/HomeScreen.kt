@file:OptIn(ExperimentalMaterial3Api::class)

package com.itskidan.currencyexchangeapp.ui.screens.home

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.core_impl.utils.Constants
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.components.AdBannerView
import com.itskidan.currencyexchangeapp.ui.screens.actualexchangerates.ActualExchangeRatesScreen
import com.itskidan.currencyexchangeapp.ui.screens.totalbalance.TotalBalanceScreen
import com.itskidan.currencyexchangeapp.ui.screens.worldtime.WorldTime
import com.itskidan.currencyexchangeapp.utils.NavigationConstants
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Set a flag to prevent sleep mode
    KeepScreenOn(context)

    // Drawer menu
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val items = viewModel.getIconsForDrawerMenu()
    val selectedItemDrawerMenu = rememberSaveable { mutableIntStateOf(1) }
    // Dropdown Menu
    val menuItems = listOf(
        R.string.home_screen_overflow_menu_actual_exchange_rate_title to Icons.Default.CurrencyExchange,
        R.string.home_screen_overflow_menu_total_balance_title to Icons.Default.MonetizationOn,
    )
    var selectedItemDropdownMenu by rememberSaveable { mutableIntStateOf(0) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.image_drawer_menu),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RectangleShape)
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    items.forEach { item ->
                        val itemName = item.name.substringAfterLast(".")
                        val label = viewModel.getLabelNameForDrawerMenu(item, context)
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(label) },
                            selected = item == items[selectedItemDrawerMenu.intValue],
                            onClick = {
                                when (itemName) {
                                    "CurrencyExchange", "MonetizationOn" -> {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        selectedItemDrawerMenu.intValue = items.indexOf(item)
                                        selectedItemDropdownMenu =
                                            selectedItemDrawerMenu.intValue - 1
                                    }

                                    "Info" -> {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        navController.navigate(
                                            "${NavigationConstants.ABOUT_APP}/" +
                                                    Constants.DRAWER_MENU_TO_ABOUT_APP
                                        )
                                    }

                                    "Settings" -> {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        navController.navigate(
                                            "${NavigationConstants.SETTINGS}/" +
                                                    Constants.DRAWER_MENU_TO_SETTINGS
                                        )
                                    }

                                    "Message" -> {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        navController.navigate(
                                            "${NavigationConstants.SEND_FEEDBACK}/" +
                                                    Constants.DRAWER_MENU_TO_SEND_FEEDBACK
                                        )
                                    }
                                    "Star" -> {
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        navController.navigate(
                                            "${NavigationConstants.DISABLE_AD}/" +
                                                    Constants.DRAWER_MENU_TO_DISABLE_AD
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
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
                            menuItems.forEachIndexed { index, (titleRes, icon) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(id = titleRes),
                                            color = if (index == selectedItemDropdownMenu) {
                                                MaterialTheme.colorScheme.primary
                                            } else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedItemDropdownMenu = index
                                        selectedItemDrawerMenu.intValue = index + 1
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (index == selectedItemDropdownMenu) {
                                                MaterialTheme.colorScheme.primary
                                            } else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        }
                    }
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
                    when (items[selectedItemDrawerMenu.intValue]) {
                        Icons.Default.CurrencyExchange -> {
                            if (selectedItemDropdownMenu != 0) {
                                selectedItemDropdownMenu = 0
                            }
                            ActualExchangeRatesScreen(
                                navController = navController,
                                scope = scope,
                            )
                        }

                        Icons.Default.MonetizationOn -> {
                            if (selectedItemDropdownMenu != 1) {
                                selectedItemDropdownMenu = 1
                            }
                            TotalBalanceScreen(
                                navController = navController,
                                scope = scope
                            )
                        }

                        Icons.Default.AccessTime -> {
                            WorldTime()
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
}

@Composable
fun KeepScreenOn(context: Context) {
    SideEffect {
        val activity = context as? Activity
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

