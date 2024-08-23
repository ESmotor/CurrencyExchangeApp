@file:OptIn(ExperimentalMaterial3Api::class)

package com.itskidan.currencyexchangeapp.ui.screens.home

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.BottomAppBar
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
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.components.AdvertisingSpace
import com.itskidan.currencyexchangeapp.ui.screens.actualexchangerates.ActualExchangeRatesScreen
import com.itskidan.currencyexchangeapp.ui.screens.totalbalance.TotalBalanceScreen
import com.itskidan.currencyexchangeapp.ui.screens.worldtime.WorldTime
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
    val selectedItemDrawerMenu = remember { mutableStateOf(items[1]) }
    // Dropdown Menu
    val menuItems = listOf(
        R.string.home_screen_overflow_menu_actual_exchange_rate_title to Icons.Default.CurrencyExchange,
        R.string.home_screen_overflow_menu_total_balance_title to Icons.Default.MonetizationOn,
        R.string.home_screen_overflow_menu_world_time_title to Icons.Default.AccessTime
    )
    var selectedItemDropdownMenu by remember { mutableIntStateOf(0) }

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
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = {
                                Text(viewModel.getLabelNameForDrawerMenu(item, context))
                            },
                            selected = item == selectedItemDrawerMenu.value,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                selectedItemDrawerMenu.value = item
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
                                        scope.launch {
                                            Toast.makeText(
                                                context,
                                                "Valera $index",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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

            bottomBar = {
                BottomAppBar {
                    AdvertisingSpace()
                }

            }
        ) { innerPadding ->
            when (selectedItemDrawerMenu.value) {
                Icons.Default.CurrencyExchange -> {
                    ActualExchangeRatesScreen(
                        innerPadding = innerPadding,
                        navController = navController,
                        scope = scope,
                    )
                }

                Icons.Default.MonetizationOn -> {
                    TotalBalanceScreen(innerPadding = innerPadding)
                }
                Icons.Default.AccessTime -> {
                    WorldTime(innerPadding = innerPadding)
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

