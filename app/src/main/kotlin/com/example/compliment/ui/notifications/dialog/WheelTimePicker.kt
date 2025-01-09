package com.example.compliment.ui.notifications.dialog

import android.util.Log
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compliment.extensions.floorMod
import com.example.compliment.ui.theme.Black
import com.example.compliment.ui.theme.GreyLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

@Composable
fun WheelTimePicker(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onHourSelected: (hour: Int) -> Unit,
    onMinuteSelected: (minute: Int) -> Unit
) {
    val hourDebounceScope = rememberCoroutineScope()
    val minuteDebounceScope = rememberCoroutineScope()

//    val recompositionCount = remember { mutableStateOf(0) }
//    recompositionCount.value++

    Log.d("RecompositionTracker", "WheelTimePicker recompositionCount")

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfiniteWheel(
            range = 0..23,
            initialTime = initialHour + 1,
            onItemSelected = { hour ->
                hourDebounceScope.launch {
                    delay(300)
                    onHourSelected(hour)
                }
            }
        )

        VerticalDivider(
            color = MaterialTheme.colorScheme.surface,
            thickness = 1.dp,
            modifier = Modifier
                .height(160.dp)
                .padding(horizontal = 16.dp)
        )

        InfiniteWheel(
            range = 0..59,
            initialTime = initialMinute + 1,
            onItemSelected = { minute ->
                minuteDebounceScope.launch {
                    delay(300)
                    onMinuteSelected(minute)
                }
            }
        )
    }
}

@Composable
fun InfiniteWheel(
    range: IntRange,
    initialTime: Int,
    onItemSelected: (Int) -> Unit
) {
    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = Int.MAX_VALUE / 2 + initialTime - 2)
    val middleItemIndex = Int.MAX_VALUE / 2
    val visibleItems = range.count()
    val items = remember { Int.MAX_VALUE }
    val snapFlingBehavior = rememberSnapFlingBehavior(listState)
    val selectedIndex = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset +
                    (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2
            val closestItem = layoutInfo.visibleItemsInfo.minByOrNull { item ->
                val itemCenter = item.offset + item.size / 2
                abs(itemCenter - viewportCenter)
            }
            closestItem?.index ?: listState.firstVisibleItemIndex
        }
    }


//    val recompositionCount = remember { mutableStateOf(0) }
//    recompositionCount.value++
    Log.d("RecompositionTracker", "InfiniteWheel recomposition")

    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior,
        modifier = Modifier.wrapContentSize()
            .height(160.dp)
            .width(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        items(items) { index ->
            val actualIndex = (index - middleItemIndex).floorMod(visibleItems)
            val value = range.elementAt(actualIndex)
            val isSelected = index == selectedIndex.value
            Text(
                text = String.format(Locale.getDefault(), "%02d", value),
                fontSize = if (isSelected) 26.sp else 20.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondary
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling) {
                    val centerIndex = selectedIndex.value
                    val actualValue = (centerIndex - middleItemIndex).floorMod(visibleItems)
                    onItemSelected(range.elementAt(actualValue))
                    Log.i("SELECTED_ITEM", "$actualValue")
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWheelTimePicker() {
    WheelTimePicker(
        0,
        0,
        onHourSelected = {},
        onMinuteSelected = {}
    )
}