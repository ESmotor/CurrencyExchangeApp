package com.itskidan.currencyexchangeapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CurrencyCodeAndName(
    modifier: Modifier = Modifier,
    currencyCode:String,
    currencyCodeStyle: TextStyle,
    currencyCodeColor: Color,
    currencyName:String,
    currencyNameStyle: TextStyle,
    currencyNameColor: Color,
    ){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = currencyCode,
            textAlign = TextAlign.Start,
            style = currencyCodeStyle,
            color = currencyCodeColor
        )
        Text(
            text = currencyName,
            textAlign = TextAlign.Start,
            style = currencyNameStyle,
            color = currencyNameColor
        )
    }
}