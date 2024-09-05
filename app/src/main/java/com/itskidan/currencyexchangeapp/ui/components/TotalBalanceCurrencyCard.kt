package com.itskidan.currencyexchangeapp.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues

@Composable
fun TotalBalanceCurrencyCard(
    modifier: Modifier = Modifier,
    currencyCode: String,
    currencyFlag: Int,
    textState: String,
    onChangeCurrency: () -> Unit

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(LocalPaddingValues.current.small)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Total balance in the selected currency:",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .weight(0.65f)
                        .padding(end = LocalPaddingValues.current.extraSmall)
                )
                CurrencyInfo(
                    currencyCode = currencyCode,
                    currencyFlag = currencyFlag,
                    onChangeCurrency = { onChangeCurrency() },
                    modifier = Modifier.weight(0.35f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(LocalPaddingValues.current.small)
                    )
                    .padding(LocalPaddingValues.current.extraSmall),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = textState,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}
