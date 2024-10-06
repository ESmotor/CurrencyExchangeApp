package com.itskidan.currencyexchangeapp.ui.screens.sendfeedback

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendFeedBackScreen(
    navController: NavHostController,
    viewModel: SendFeedbackViewModel = viewModel(),
    locationOfRequest: String,
) {
    Timber.tag("MyLog").d("locationOfRequest:$locationOfRequest")

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    var isEnableBackBtn by remember { mutableStateOf(true) }

    val textStateMessage = remember { mutableStateOf(TextFieldValue("")) }

    var titleText by remember { mutableStateOf("") }
    titleText = stringResource(R.string.feedback_screen_title_help)

    var showRatingDialog by remember { mutableStateOf(false) }

    var selectedOption by remember { mutableStateOf("") }

    var continueOption by remember { mutableStateOf("") }

    var isContinueScreen by remember { mutableStateOf(false) }

    var buttonText by remember { mutableStateOf("") }
    buttonText = stringResource(R.string.feedback_screen_btn_caption_continue)

    viewModel.loadInterstitialAd(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.send_feedback_screen_top_app_bar_title))
                },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isContinueScreen) {
                            if (isEnableBackBtn) {
                                isEnableBackBtn = false
                                navController.popBackStack()

                                scope.launch {
                                    delay(1000)
                                    isEnableBackBtn = true
                                }
                            }
                        } else {
                            isContinueScreen = false
                            continueOption = ""
                            buttonText = context.getString(R.string.feedback_screen_btn_caption_continue)
                            titleText = context.getString(R.string.feedback_screen_title_help)
                            textStateMessage.value = TextFieldValue("")
                        }

                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
                .imePadding()
                .consumeWindowInsets(innerPadding)

        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = titleText,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )

            MainContent(
                selectedOption = selectedOption,
                continueOption = continueOption,
                textStateMessage = textStateMessage.value,
                onClickOption = { option ->
                    selectedOption = option
                },
                onTextChange = { newMessage ->
                    textStateMessage.value = newMessage
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalPaddingValues.current.medium)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = (continueOption.isEmpty() && selectedOption.isNotEmpty())
                            || (continueOption.isNotEmpty() && textStateMessage.value.text.isNotEmpty()),
                    onClick = {
                        isContinueScreen = true
                        buttonText = context.getString(R.string.feedback_screen_btn_text_send)
                        if (continueOption.isEmpty()) {
                            when (selectedOption) {
                                "Option 1" -> {
                                    titleText = context.getString(R.string.feedback_screen_title_problem)
                                    continueOption = selectedOption
                                }

                                "Option 2" -> {
                                    titleText = context.getString(R.string.feedback_screen_title_idea)
                                    continueOption = selectedOption
                                }

                                "Option 3" -> {
                                    showRatingDialog = true
                                }
                            }
                        } else {
                            when (continueOption) {
                                "Option 1" -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.feedback_screen_message_has_been_sent),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    viewModel.sendUserFeedback("problem",textStateMessage.value.text)

                                }

                                "Option 2" -> {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.feedback_screen_message_has_been_sent),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    viewModel.sendUserFeedback("idea",textStateMessage.value.text)
                                }
                            }
                            isContinueScreen = false
                            continueOption = ""
                            buttonText = context.getString(R.string.feedback_screen_btn_caption_continue)
                            titleText = context.getString(R.string.feedback_screen_title_help)
                            textStateMessage.value = TextFieldValue("")
                        }
                    }
                ) {
                    Text(
                        text = buttonText,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
            if (showRatingDialog) {
                ShowRatingDialog(
                    onClickOutside = { showRatingDialog = false },
                    onConfirmClick = { showRatingDialog = false }
                )
            }
        }
    }
}

@Composable
fun MainContent(
    selectedOption: String,
    continueOption: String,
    textStateMessage: TextFieldValue,
    onClickOption: (String) -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
) {
    when (continueOption) {
        "Option 1", "Option 2" -> {
            ContinueFeedbackComponent(continueOption, textStateMessage, onTextChange)
        }

        else -> {
            FeedbackComponentOption(
                text = stringResource(R.string.feedback_screen_i_have_a_problem),
                isSelected = selectedOption == "Option 1",
                onClick = { onClickOption("Option 1") }
            )

            FeedbackComponentOption(
                text = stringResource(R.string.feedback_screen_i_have_an_idea),
                isSelected = selectedOption == "Option 2",
                onClick = { onClickOption("Option 2") }
            )

            FeedbackComponentOption(
                text = stringResource(R.string.feedback_screen_i_like_your_application),
                isSelected = selectedOption == "Option 3",
                onClick = { onClickOption("Option 3") }
            )
        }
    }
}

@Composable
fun FeedbackComponentOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LocalPaddingValues.current.extraSmall)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onClick() }
        )
        Text(
            modifier = Modifier
                .padding(end = LocalPaddingValues.current.medium),
            text = text,
        )

    }
}

@Composable
fun ContinueFeedbackComponent(
    continueOption: String,
    textStateMessage: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
) {
    val placeholderProblemText = stringResource(R.string.feedback_screen_problem_placeholder)
    val placeholderIdeaText = stringResource(R.string.feedback_screen_idea_placeholder)
    val placeholderOtherText = stringResource(R.string.feedback_screen_other_placeholder)

    val placeholder: String = when (continueOption) {
        "Option 1" -> placeholderProblemText
        "Option 2" -> placeholderIdeaText
        else -> placeholderOtherText
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(LocalConfiguration.current.screenHeightDp.dp / 3)
    ) {
        TextField(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                    shape = MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium),
            value = textStateMessage,
            onValueChange = onTextChange,
            placeholder = { Text(placeholder) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun ShowRatingDialog(
    onClickOutside: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    ) {

    val title= stringResource(R.string.feedback_screen_rating_dialog_title)
    val dialogTextFirstLine = stringResource(R.string.feedback_screen_first_line_dialog_text)
    val dialogTextSecondLine = stringResource(R.string.feedback_screen_second_line_dialog_text)
    val confirmButtonText = stringResource(R.string.feedback_screen_confirm_btn_text)

    AlertDialog(
        onDismissRequest = { onClickOutside() },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                Text(text = dialogTextFirstLine)
                Text(
                    modifier = Modifier.padding(vertical = LocalPaddingValues.current.small),
                    text = dialogTextSecondLine
                )
                Row {
                    val starColor = Color(0xFFECD317)
                    repeat(5){
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Default.Star,
                            tint = starColor,
                            contentDescription = "Rating Star",
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmClick() }
            ) {
                Text(confirmButtonText)
            }
        }
    )
}
