package com.itskidan.currencyexchangeapp.ui.screens.totalbalance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TotalBalanceScreen(
    viewModel: TotalBalanceViewModel = viewModel(),
    innerPadding: PaddingValues,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Red)
        .padding(innerPadding))
}