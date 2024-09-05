package com.itskidan.currencyexchangeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
@Composable
fun CurrencyCard(
    currencyCode: String,
    currencyFlag: Int,
    textFieldValue: TextFieldValue,
    isFocused: Boolean = false,
    onChangeCurrency: (String,String,Boolean) -> Unit,
    onFocusChange: () -> Unit,
    onTextChange: (TextFieldValue) -> Unit
) {
    Card(
        shape = RectangleShape,
        modifier = Modifier.height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isFocused) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface
                )
                .clickable(onClick = {}),
            horizontalArrangement = Arrangement.spacedBy(LocalPaddingValues.current.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyInfo(
                currencyCode = currencyCode,
                currencyFlag = currencyFlag,
                isFocused = isFocused,
                onChangeCurrency = { onChangeCurrency(currencyCode,textFieldValue.text,isFocused) }
            )
            CurrencyInput(
                modifier = Modifier.weight(1f),
                isFocused = isFocused,
                textFieldValue = textFieldValue,
                onTextChange = onTextChange,
                onFocusChange = onFocusChange,
            )
        }
    }
}

@Composable
fun CurrencyInfo(
    modifier: Modifier = Modifier,
    currencyCode: String,
    currencyFlag: Int,
    isFocused: Boolean = false,
    onChangeCurrency: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = onChangeCurrency),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalPaddingValues.current.small),
    ) {
        CurrencyFlag(
            currencyFlag = currencyFlag,
            size = 50.dp,
            borderSize = 2.dp,
            borderColor = MaterialTheme.colorScheme.primaryContainer
        )

        Text(
            text = currencyCode,
            textAlign = TextAlign.Start,
            color = if (isFocused) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )

        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            tint = if (isFocused) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            contentDescription = "Triangle list icon",
        )
    }
}

@Composable
fun CurrencyInput(
    modifier: Modifier = Modifier,
    isFocused: Boolean,
    textFieldValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onFocusChange: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = LocalPaddingValues.current.extraSmall,
                shape = MaterialTheme.shapes.small
            )
            .clip(RoundedCornerShape(LocalPaddingValues.current.small))
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                horizontal = LocalPaddingValues.current.small,
                vertical = LocalPaddingValues.current.small
            ),
        contentAlignment = Alignment.BottomStart
    ) {
        CompositionLocalProvider(LocalTextInputService provides null) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = onTextChange,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                ) + MaterialTheme.typography.titleLarge,
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onFocusChange()
                        }
                    }
                    .focusRequester(focusRequester),
            )
        }
    }
}