@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.itskidan.currencyexchangeapp.ui.calculator

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun CalculatorScreen(
    navController: NavHostController,
    viewModel: CalculatorScreenViewModel = viewModel(),
    currencyCode: String,
    currencyValue: String
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val textStateFromKeyboard = remember { mutableStateOf(currencyValue) }
    val resultState = remember { mutableStateOf("") }
    val isDivideByZero = remember { mutableStateOf(false) }
    val isAvailableToDone = remember { mutableStateOf(true) }

    Timber.tag("MyLog").d("code: $currencyCode, value: $currencyValue")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.calculator_screen_top_app_bar_title))
                },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar {
                AdvertisingSpace()
            }

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(30f)
            )
            {
                InputAndCalculation(
                    textStateFromKeyboard = textStateFromKeyboard.value,
                    resultTextState = resultState.value
                )
            }
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth(0.9f),

                    )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(70f)
            )
            {
                CalculatorKeyboard(
                    scope = scope,
                    viewModel = viewModel,
                    textState = textStateFromKeyboard.value,
                    isAvailableToDone = isAvailableToDone.value,
                    onInputTextChange = { newText ->
                        textStateFromKeyboard.value = newText
                    },
                    onResultTextChange = { newResult ->
                        resultState.value = newResult
                    },
                    onEqualOrDoneClick = {
                        if (isAvailableToDone.value) {
                            scope.launch {
                                viewModel.saveSelectedLastState(
                                    code = currencyCode,
                                    value = textStateFromKeyboard.value.replace(",", ".")
                                )
                                navController.popBackStack()
                            }

                        } else {
                            if (isDivideByZero.value) {
                                Toast.makeText(
                                    context,
                                    "You can't divide by zero",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                textStateFromKeyboard.value = resultState.value
                                resultState.value = ""
                            }
                        }


                    },
                    onDivideByZero = { newState ->
                        isDivideByZero.value = newState
                    },
                    onAvailableToDoneChange = { newState ->
                        isAvailableToDone.value = newState
                    }
                )
            }
        }
    }


}

@Composable
fun InputAndCalculation(
    textStateFromKeyboard: String,
    resultTextState: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(60f)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            InputText(
                textStateFromKeyboard = textStateFromKeyboard
            )
        }

        Box(
            modifier = Modifier
                .weight(20f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CalcText(
                resultTextState = resultTextState
            )
        }
    }
}

@Composable
fun InputText(
    textStateFromKeyboard: String
) {
    val originalStyle = MaterialTheme.typography.displayMedium
    Box {
        Text(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            text = textStateFromKeyboard,
            textAlign = TextAlign.End,
            maxLines = 3,
            color = MaterialTheme.colorScheme.onSurface,
            style = originalStyle,
        )
    }
}

@Composable
fun CalcText(
    resultTextState: String
) {
    Box {
        Text(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            text = resultTextState,
            textAlign = TextAlign.End,
            maxLines = 1,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun CalculatorKeyboard(
    scope: CoroutineScope,
    viewModel: CalculatorScreenViewModel,
    isAvailableToDone: Boolean,
    textState: String,
    onInputTextChange: (String) -> Unit,
    onResultTextChange: (String) -> Unit,
    onEqualOrDoneClick: () -> Unit,
    onDivideByZero: (Boolean) -> Unit,
    onAvailableToDoneChange: (Boolean) -> Unit
) {
    LaunchedEffect(textState) {
        val textToDouble = textState.replace(",", ".")
        onAvailableToDoneChange(textToDouble.toDoubleOrNull() != null || textToDouble == "")
    }
    val keys = listOf(
        listOf("C", "+/-", "%", "÷"),
        listOf("1", "2", "3", "×"),
        listOf("4", "5", "6", "—"),
        listOf("7", "8", "9", "+"),
        listOf(",", "0", "X", "="),

        )
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.9f),
            verticalArrangement = Arrangement.spacedBy(LocalPaddingValues.current.small)
        ) {
            keys.forEach { rowKeys ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(LocalPaddingValues.current.small)
                    ) {
                        rowKeys.forEach { key ->
                            CalcKeyButton(
                                key = key,
                                isAvailableToDone = isAvailableToDone,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (key == "=") {
                                        scope.launch {
                                            onEqualOrDoneClick()
                                        }
                                    } else {
                                        scope.launch {
                                            val newTextState =
                                                viewModel.validateCalcInput(textState, key)
                                            val result =
                                                if (viewModel.calculateExpression(newTextState) == "Divide by zero") {
                                                    onDivideByZero(true)
                                                    ""
                                                } else {
                                                    onDivideByZero(false)
                                                    viewModel.calculateExpression(newTextState)
                                                }
                                            onResultTextChange(result)
                                            onInputTextChange(newTextState)
                                        }
                                    }
                                },
                                onLongClick = {
                                    if (key == "X") {
                                        scope.launch {
                                            val newTextState =
                                                viewModel.validateCalcInput(textState, "C")
                                            val result =
                                                if (viewModel.calculateExpression(newTextState) == "Divide by zero") {
                                                    onDivideByZero(true)
                                                    ""
                                                } else {
                                                    onDivideByZero(false)
                                                    viewModel.calculateExpression(newTextState)
                                                }
                                            onResultTextChange(result)
                                            onInputTextChange(newTextState)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CalcKeyButton(
    key: String,
    isAvailableToDone: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() },
                indication = rememberRipple(
                    bounded = true,
                    color = MaterialTheme.colorScheme.primary
                ),
                interactionSource = remember { MutableInteractionSource() }
            ),

        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "X" ->
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(0.4f),
                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Backspace icon",
                    )
                }

            "C" -> {
                Text(
                    text = key,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            "×" -> {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.4f),
                    imageVector = Icons.Default.Close,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Backspace icon",
                )
            }

            "+" -> {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.4f),
                    imageVector = Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Backspace icon",
                )
            }

            "÷" -> {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.4f),
                    imageVector = ImageVector.vectorResource(id = R.drawable.division_vector),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Backspace icon",
                )
            }

            "—" -> {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.4f),
                    imageVector = Icons.Default.Remove,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Backspace icon",
                )
            }

            "=" -> {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(0.4f),
                    imageVector = if (isAvailableToDone) {
                        Icons.Default.Done
                    } else {
                        ImageVector.vectorResource(id = R.drawable.equal_vector)
                    },
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Backspace icon",
                )
            }

            "%" -> {
                Text(
                    text = key,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            "+/-" -> {
                Text(
                    text = key,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            else ->
                Text(
                    text = key,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.headlineMedium
                )
        }
    }
}

@Composable
fun AdvertisingSpace() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Advertising Space",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge
        )
    }
}



