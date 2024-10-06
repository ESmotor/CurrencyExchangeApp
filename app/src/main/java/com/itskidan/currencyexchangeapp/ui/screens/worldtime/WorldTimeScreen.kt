package com.itskidan.currencyexchangeapp.ui.screens.worldtime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WorldTime(
    viewModel: WorldTimeViewModel = viewModel(),
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Green)
    )
}