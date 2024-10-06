package com.itskidan.currencyexchangeapp.ui.screens.dismissad

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.currencyexchangeapp.R
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun DismissAdScreen(
    navController: NavHostController,
    viewModel: DismissAdViewModel = viewModel(),
    locationOfRequest: String,
) {
    Timber.tag("MyLog").d("locationOfRequest:$locationOfRequest")

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val dismissAdPrice = viewModel.getDismissAdPrice()

    var isEnableCloseBtn by remember { mutableStateOf(true) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ImageHeaderWithClose(
                    onCloseClick = {
                        if (isEnableCloseBtn) {
                            isEnableCloseBtn = false
                            navController.popBackStack()

                            scope.launch {
                                delay(700)
                                isEnableCloseBtn = true
                            }
                        }
                    }
                )

                ContentWithButton(
                    priceText = dismissAdPrice,
                    onButtonClick = {
                        Toast.makeText(
                            context,
                            "Congratulations, you have disabled the ads",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

            }
        }
    }

}

@Composable
fun ImageHeaderWithClose(
    onCloseClick: () -> Unit
) {
    Box {
        Image(
            painter = painterResource(id = R.drawable.disable_ad),
            contentDescription = "Header Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RectangleShape)
        )
        Icon(
            modifier = Modifier
                .size(50.dp)
                .padding(LocalPaddingValues.current.small)
                .clickable { onCloseClick() },
            imageVector = Icons.Default.Close,
            tint = MaterialTheme.colorScheme.scrim,
            contentDescription = "Close and back",
        )
    }
}

@Composable
fun ContentWithButton(
    priceText: String = "99,00 USD",
    onButtonClick: () -> Unit
) {

    Column(
        modifier = Modifier.padding(
            horizontal = LocalPaddingValues.current.large,
            vertical = LocalPaddingValues.current.medium
        )
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LocalPaddingValues.current.medium),
            text = stringResource(R.string.dismiss_screen_main_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LocalPaddingValues.current.medium),
            text = priceText,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold
            )
        )

        ReasonDisableAd(
            titleText = stringResource(R.string.dismiss_screen_reason_one_title),
            subtitleText = stringResource(R.string.dismiss_screen_reason_one_subtitle)
        )

        ReasonDisableAd(
            titleText = stringResource(R.string.dismiss_screen_reason_two_title),
            subtitleText = stringResource(R.string.dismiss_screen_reason_two_subtitle)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            onClick = {
                onButtonClick()
            }
        ) {
            Text(
                text = stringResource(R.string.dismiss_screen_button_caption),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    }
}

@Composable
fun ReasonDisableAd(
    titleText: String,
    subtitleText: String
) {
    Row(
        modifier = Modifier
            .padding(vertical = LocalPaddingValues.current.medium),
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = LocalPaddingValues.current.medium),
            text = "●",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = titleText,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = subtitleText,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}