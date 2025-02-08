package com.glazer.compliment.ui.notifications

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.glazer.compliment.R
import com.glazer.compliment.models.NotificationScheduleWithFlow
import com.glazer.compliment.ui.elements.CustomSwitch

@Composable
fun ScheduleItem(
    schedule: NotificationScheduleWithFlow,
    daysText: String,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val swipeState = rememberSwipeToDismissBoxState()
    val checkedState = schedule.isActive.collectAsStateWithLifecycle(false)

    Log.d("RecompositionTracker", "NotificationItem recomposed")

    val icon = painterResource(R.drawable.icon_delete)
    val alignment: Alignment = Alignment.CenterEnd

    val colorBackground: Color = MaterialTheme.colorScheme.error

    SwipeToDismissBox(
        modifier = Modifier.animateContentSize(),
        state = swipeState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 4.dp)
                    .background(colorBackground, RoundedCornerShape(20.dp))
            ) {
                Icon(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .padding(end = 24.dp),
                    painter = icon,
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onEdit() },
            elevation = CardDefaults.cardElevation(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = schedule.time,
                        fontSize = 18.sp,
                        color = if (checkedState.value) MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.inverseSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = daysText,
                        fontSize = 14.sp,
                        color = if (checkedState.value) MaterialTheme.colorScheme.onSecondary
                        else MaterialTheme.colorScheme.inverseSurface
                    )
                }

                CustomSwitch(checkedState.value) {
                    onCheckedChange(it)
                }
            }
        }
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onDelete()
        }

        SwipeToDismissBoxValue.StartToEnd -> {}

        SwipeToDismissBoxValue.Settled -> {}
    }
}