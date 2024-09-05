package com.itskidan.currencyexchangeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun CurrencyFlag(
    modifier: Modifier = Modifier,
    currencyFlag: Int,
    size: Dp,
    borderSize: Dp,
    borderColor: Color
) {
    Image(
        painter = painterResource(id = currencyFlag),
        contentDescription = "Currency Flag Country",
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(borderSize, borderColor, CircleShape),
        contentScale = ContentScale.Crop
    )
}