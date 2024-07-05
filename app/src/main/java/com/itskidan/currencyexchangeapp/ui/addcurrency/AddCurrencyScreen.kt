package com.itskidan.currencyexchangeapp.ui.addcurrency

import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.itskidan.core_api.entity.Currency
import com.itskidan.currencyexchangeapp.ui.theme.LocalPaddingValues
import com.itskidan.currencyexchangeapp.utils.CurrencyUtils
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCurrencyScreen(
    navController: NavHostController,
    viewModel: AddCurrencyScreenViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    //create ScreenSize
    val screenWidthInDp = getScreenWidthInDp(context)
    val screenHeightInDp = getScreenHeightInDp(context)
    //create Data
    var dataListActive: SnapshotStateList<Currency> = viewModel.dataListActive
    var dataListPassive: MutableList<Currency> = viewModel.dataListPassive
    var searchText by remember { mutableStateOf("") }
    val filteredActiveListForSearch =
        dataListActive.filter {
            it.currencyCode.contains(searchText, ignoreCase = true)
                    || it.currencyName.contains(searchText, ignoreCase = true)
        }
    val filteredPassiveListForSearch =
        dataListPassive.filter {
            it.currencyCode.contains(searchText, ignoreCase = true) ||
                    it.currencyName.contains(searchText, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            "Back"
                        )
                    }
                },
                modifier = Modifier.statusBarsPadding(),
                title = {
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        decorationBox = { innerTextField ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(LocalPaddingValues.current.small)
                            ) {
                                if (searchText.isEmpty()) {
                                    Text("Search...", color = MaterialTheme.colorScheme.outline)
                                }
                                innerTextField()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
            )
        },

        ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {


                    //ActiveList
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(filteredActiveListForSearch) { currency ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(color = Color.Gray),
                                        onClick = {
                                            scope.launch {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Item Click",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        }
                                    )
                            ) {
                                // This is icon for checkbox
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Default.CheckBox,
                                        contentDescription = "CheckBox icon",
                                    )
                                }
                                // This is flag
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width(80.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = CurrencyUtils.currencyFlagMap[currency.currencyCode]
                                                ?: 0
                                        ),
                                        contentDescription = "Currency Flag Country",
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center,
                                    )
                                }
                                // This is name of currency
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width((screenWidthInDp - 150).dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = currency.currencyCode,
                                                textAlign = TextAlign.Start,
                                                style = MaterialTheme.typography.titleLarge

                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = currency.currencyName,
                                                textAlign = TextAlign.Start,
                                                style = MaterialTheme.typography.titleMedium

                                            )
                                        }
                                    }


                                }
                                // change order icon
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(80.dp)
                                        .clickable(
                                            onClick = {
                                                scope.launch {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Change order",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .height(30.dp)
                                            .width(30.dp),
                                        imageVector = Icons.Default.FormatLineSpacing,
                                        contentDescription = "Change Order",
                                    )
                                }
                            }
                        }


                        // Divider between two lists
                        item {
                            Divider(
                                color = MaterialTheme.colorScheme.outline,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                            )
                        }


                        //Passive currency
                        items(filteredPassiveListForSearch) { currency ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = rememberRipple(color = Color.Gray),
                                        onClick = {
                                            scope.launch {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        "Item Click",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        }
                                    )
                            ) {
                                // This is icon for checkbox
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Default.CheckBoxOutlineBlank,
                                        contentDescription = "CheckBox icon",
                                    )
                                }
                                // This is flag
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width(80.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = CurrencyUtils.currencyFlagMap[currency.currencyCode]
                                                ?: 0
                                        ),
                                        contentDescription = "Currency Flag Country",
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center,
                                    )
                                }
                                // This is name of currency
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .width((screenWidthInDp - 150).dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = currency.currencyCode,
                                                textAlign = TextAlign.Start,
                                                style = MaterialTheme.typography.titleLarge

                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                text = currency.currencyName,
                                                textAlign = TextAlign.Start,
                                                style = MaterialTheme.typography.titleMedium

                                            )
                                        }
                                    }


                                }
                                // change order icon
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(80.dp)
                                        .clickable(
                                            onClick = {
                                                scope.launch {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Change order",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                }
                                            }),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .height(30.dp)
                                            .width(30.dp),
                                        imageVector = Icons.Default.FormatLineSpacing,
                                        contentDescription = "Change Order",
                                    )
                                }
                            }
                        }
                    }
                }

            }

        }


    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddCurrencyScreen(
//    navController: NavHostController,
//    viewModel: AddCurrencyScreenViewModel = viewModel(),
//
//    ) {
//    val context = LocalContext.current
//
//    val scope = rememberCoroutineScope()
//
//    //create ScreenSize
//    val screenWidthInDp = getScreenWidthInDp(context)
//    val screenHeightInDp = getScreenHeightInDp(context)
//    //create Data
//    val currencyList by viewModel.databaseFromDB.collectAsState(initial = emptyList())
//    var searchText by remember { mutableStateOf("") }
//    val filteredCurrencyList =
//        currencyList.filter { it.currencyCode.contains(searchText, ignoreCase = true) }
//            .toMutableList()
//    val onMove: (Int, Int) -> Unit =
//        { fromIndex, toIndex -> filteredCurrencyList.move(fromIndex, toIndex) }
//    var overScrollJob by remember { mutableStateOf<Job?>(null) }
//    val dragDropListState = rememberDragDropListState(onMove = onMove)
//
//
//
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            Icons.Default.ArrowBackIosNew,
//                            "Back"
//                        )
//                    }
//                },
//                modifier = Modifier.statusBarsPadding(),
//                title = {
//                    BasicTextField(
//                        value = searchText,
//                        onValueChange = { searchText = it },
//                        singleLine = true,
//                        textStyle = MaterialTheme.typography.titleLarge,
//                        maxLines = 1,
//                        decorationBox = { innerTextField ->
//                            Box(
//                                Modifier
//                                    .fillMaxWidth()
//                                    .padding(LocalPaddingValues.current.small)
//                            ) {
//                                if (searchText.isEmpty()) {
//                                    Text("Search...", color = MaterialTheme.colorScheme.outline)
//                                }
//                                innerTextField()
//                            }
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                },
//            )
//        },
//
//        ) { innerPadding ->
//        Box(
//            contentAlignment = Alignment.Center,
//        ) {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(innerPadding)
//                    .pointerInput(Unit) {
//                        detectDragGesturesAfterLongPress(
//                            onDrag = { change, offset ->
//                                change.consumeAllChanges()
//                                dragDropListState.onDrag(offset = offset)
//                                if (overScrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
//                                dragDropListState
//                                    .checkForOverScroll()
//                                    .takeIf { it != 0f }
//                                    ?.let {
//                                        overScrollJob = scope.launch {
//                                            dragDropListState.lazyListState.scrollBy(it)
//                                        }
//                                    } ?: kotlin.run { overScrollJob?.cancel() }
//                            },
//                            onDragStart = { offset -> dragDropListState.onDragStart(offset) },
//                            onDragEnd = { dragDropListState.onDragInterrupted() },
//                            onDragCancel = { dragDropListState.onDragInterrupted() }
//
//                        )
//
//                    }
//                    .fillMaxSize()
//                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
//                state = dragDropListState.lazyListState
//            ) {
//                itemsIndexed(filteredCurrencyList) { index, currency ->
//                    SearchListItemForAddCurrencyList(
//                        dragDropListState = dragDropListState,
//                        index = index,
//                        currency = currency,
//                        screenWidthInDp = screenWidthInDp,
//                        screenHeightInDp = screenHeightInDp,
//                        currencyName = viewModel.getCurrencyName(currency.currencyCode)
//                    )
//                }
//            }
//        }
//
//
//    }
//}
//
//@Composable
//fun SearchListItemForAddCurrencyList(
//    dragDropListState: DragDropListState,
//    index: Int,
//    currency: Currency,
//    modifier: Modifier = Modifier,
//    screenWidthInDp: Int,
//    screenHeightInDp: Int,
//    currencyName: String
//) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    // currency settings
//    val currencyFlagResourceId = CurrencyUtils.currencyFlagMap[currency.currencyCode] ?: 0
//    // current screen size in dp
//    // height settings
//    val minBoxHeight = 80
//    val boxHeight =
//        if (screenHeightInDp / 11 <= minBoxHeight) minBoxHeight else screenHeightInDp / 11
//    // width settings
//    val firstBoxWidth = (screenWidthInDp / 13.5).toInt()
//    val secondBoxWidth =
//        if (screenHeightInDp / 11 <= minBoxHeight) minBoxHeight else screenHeightInDp / 11
//    val fourBoxWidth = (screenWidthInDp / 9)
//    val thirdBoxWidth =
//        screenWidthInDp - firstBoxWidth - secondBoxWidth - fourBoxWidth
//
//    Row(
//        modifier = modifier
//            .composed {
//                val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
//                    index == dragDropListState.currentIndexOfDraggedItem
//                }
//                Modifier.graphicsLayer {
//                    translationY = offsetOrNull ?: 0f
//                }
//            }
//            .fillMaxWidth()
//            .height(boxHeight.dp)
//            .clickable(
//                interactionSource = remember { MutableInteractionSource() },
//                indication = rememberRipple(color = Color.Gray),
//                onClick = {
//                    scope.launch {
//                        Toast
//                            .makeText(context, "Item Click", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            )
//    ) {
//        // This is icon for checkbox
//        Box(
//            modifier = Modifier
//                .width(firstBoxWidth.dp)
//                .height(boxHeight.dp),
//            contentAlignment = Alignment.Center,
//        ) {
//            Icon(
//                modifier = Modifier.fillMaxSize(),
//                imageVector = Icons.Default.CheckBox,
//                contentDescription = "CheckBox icon",
//            )
//        }
//        // This is flag
//        Box(
//            modifier = Modifier
//                .height(boxHeight.dp)
//                .width(secondBoxWidth.dp),
//            contentAlignment = Alignment.Center,
//        ) {
//            Image(
//                painter = painterResource(id = currencyFlagResourceId),
//                contentDescription = "Currency Flag Country",
//                modifier = Modifier
//                    .width((boxHeight / 8 * 5).dp)
//                    .height((boxHeight / 8 * 5).dp),
//                contentScale = ContentScale.Crop,
//                alignment = Alignment.Center,
//            )
//        }
//        // This is name of currency
//        Box(
//            modifier = Modifier
//                .height(boxHeight.dp)
//                .width(thirdBoxWidth.dp),
//            contentAlignment = Alignment.Center,
//        ) {
//            Column {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentAlignment = Alignment.Center,
//                ) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = currency.currencyCode,
//                        textAlign = TextAlign.Start,
//                        style = MaterialTheme.typography.titleLarge
//
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    contentAlignment = Alignment.Center,
//                ) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = currencyName,
//                        textAlign = TextAlign.Start,
//                        style = MaterialTheme.typography.titleMedium
//
//                    )
//                }
//            }
//
//
//        }
//        // change order icon
//        Box(
//            modifier = Modifier
//                .width(fourBoxWidth.dp)
//                .height(boxHeight.dp)
//                .clickable(
//                    onClick = {
//                        scope.launch {
//                            Toast
//                                .makeText(context, "Change order", Toast.LENGTH_SHORT)
//                                .show()
//                        }
//                    }),
//            contentAlignment = Alignment.Center,
//        ) {
//            Icon(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .height(firstBoxWidth.dp)
//                    .width(firstBoxWidth.dp),
//                imageVector = Icons.Default.FormatLineSpacing,
//                contentDescription = "Change Order",
//            )
//        }
//    }
//}

fun getScreenWidthInDp(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val result = (displayMetrics.widthPixels / displayMetrics.density).toInt()
    Timber.tag("MyLog").d("width = %s", result)
    return result
}

fun getScreenHeightInDp(context: Context): Int {
    val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    val result = (displayMetrics.heightPixels / displayMetrics.density).toInt()
    Timber.tag("MyLog").d("height = %s", result)
    return result
}