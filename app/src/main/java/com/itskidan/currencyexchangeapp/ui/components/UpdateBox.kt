package com.itskidan.currencyexchangeapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun UpdateBox(
    isUpdating:Boolean,
    updateTime:String
){
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