package com.itskidan.currencyexchangeapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun KeyboardForTyping(
    scope: CoroutineScope,
    textState: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit

) {
    val keys = listOf(
        listOf("C", "1", "2", "3"),
        listOf("Add", "4", "5", "6"),
        listOf("Upd", "7", "8", "9"),
        listOf("Calc", ".", "0", "X")
    )

    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize()
    ) {
        keys.forEach { rowKeys ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowKeys.forEach { key ->
                        KeyButton(
                            text = key,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                scope.launch {
                                    val (newText, cursorPos) = validateInput(
                                        textState = textState,
                                        newValue = key,
                                    )
                                    onTextChange(
                                        TextFieldValue(
                                            text = newText,
                                            selection = TextRange(cursorPos)
                                        )
                                    )
                                }
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .clickable(
                onClick = { onClick() },
                indication = rememberRipple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                ),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            "X" ->
                Box(
                    modifier = Modifier
                        .padding(vertical = LocalPaddingValues.current.medium)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace icon",
                    )
                }
            "Add"->
                Box(
                    modifier = Modifier
                        .padding(vertical = LocalPaddingValues.current.small)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = "Add Currency icon",
                    )
                }
            "Upd"->
                Box(
                    modifier = Modifier
                        .padding(vertical = LocalPaddingValues.current.small)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Add Currency icon",
                    )
                }
            "Calc"->
                Box(
                    modifier = Modifier
                        .padding(vertical = LocalPaddingValues.current.small)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Add Currency icon",
                    )
                }
            "C"->{
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            else ->
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleLarge
                )
        }
    }
}

fun validateInput(textState: TextFieldValue, newValue: String): Pair<String, Int> {
    val result: String
    val stringBuilder: StringBuilder = StringBuilder(textState.text)
    var newCursorPos: Int = textState.selection.start

    when (newValue) {
        "X" -> {
            if (newCursorPos > 0) {
                stringBuilder.deleteCharAt(newCursorPos - 1)
                newCursorPos -= 2
                if (stringBuilder.startsWith('.')) {
                    stringBuilder.insert(0, "0")
                    newCursorPos += 1
                }
            } else {
                newCursorPos = -1
            }
        }

        "." -> {
            if (!(stringBuilder.contains('.'))) {
                stringBuilder.insert(newCursorPos, newValue)
            } else {
                newCursorPos -= 1
            }
        }

        in "0".."9" -> {
            stringBuilder.insert(newCursorPos, newValue)
        }

        "C" -> {
            return Pair("", 0)
        }

        else -> {
            newCursorPos -= 1
        }
    }

    if (stringBuilder.contains('.')) {
        val dotIndex = stringBuilder.indexOf('.')
        val integerPart = StringBuilder(stringBuilder.substring(0, dotIndex))
        val decimalPart = StringBuilder(stringBuilder.substring(dotIndex + 1))
        if (integerPart.length > 1 && integerPart.startsWith('0')) {
            result = integerPart.replaceFirst(Regex("^0+"), "")
            return if (result.isNotEmpty()) {
                if (newCursorPos == 1) {
                    Pair("$result.$decimalPart", 1)
                } else {
                    Pair("$result.$decimalPart", 0)
                }
            } else {
                if (newCursorPos == 1) {
                    Pair("0$result.$decimalPart", 1)
                } else {
                    Pair("0$result.$decimalPart", 0)
                }

            }
        }
        if (integerPart.isEmpty()) {
            return Pair("0.$decimalPart", 2)
        }
    } else {
        if (stringBuilder.length > 1 && stringBuilder.startsWith('0')) {
            result = stringBuilder.replaceFirst(Regex("^0+"), "")
            return if (result.isNotEmpty()) {
                if (newCursorPos == 1) {
                    Pair(result, 1)
                } else {
                    Pair(result, 0)
                }
            } else {
                Pair("0", 1)
            }
        }
    }

    return Pair(stringBuilder.toString(), newCursorPos + 1)
}
