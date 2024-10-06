package com.itskidan.currencyexchangeapp.ui.screens.adoutapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.core.Utils.TimeUtils
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.components.AdBannerView
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    navController: NavHostController,
    viewModel: AboutAppViewModel = viewModel(),
    locationOfRequest: String,
) {
    Timber.tag("MyLog").d("locationOfRequest:$locationOfRequest")

    val scope = rememberCoroutineScope()

    val versionState by viewModel.version.collectAsState(initial = "1.0.0")

    var isEnableBackBtn by remember { mutableStateOf(true) }

    //Checking for last update time
    val lastUpdateTimeRates by viewModel.lastUpdateTimeRates.collectAsState(initial = 0L)
    var updateTimeState by remember { mutableStateOf("") }

    LaunchedEffect(lastUpdateTimeRates) {
        if (lastUpdateTimeRates > 0L) {
            updateTimeState = TimeUtils.getFormattedCurrentTime(lastUpdateTimeRates)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.about_app_screen_top_app_bar_title))
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
        val title = stringResource(R.string.about_app_screen_top_app_bar_title)
        val description = stringResource(R.string.about_app_screen_description)
        val version = stringResource(R.string.about_app_screen_version) + versionState
        val updateTime = stringResource(R.string.about_app_screen_update) + updateTimeState

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
                AboutAppContent(
                    title = title,
                    description = description,
                    version = version,
                    updateTime = updateTime

                )
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
fun AboutAppContent(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    version: String,
    updateTime: String,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LocalPaddingValues.current.medium),
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall

        )
        Text(
            text = description,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(0.85f),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = version,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LocalPaddingValues.current.small),
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium

        )
        Text(
            text = updateTime,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LocalPaddingValues.current.small),
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.titleMedium
        )
    }
}