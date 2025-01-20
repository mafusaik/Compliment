package com.example.compliment.ui.notifications

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compliment.R
import com.example.compliment.models.NotificationScheduleWithFlow
import com.example.compliment.models.NotificationsEvent
import com.example.compliment.models.NotificationsUiState
import com.example.compliment.ui.notifications.dialog.ScheduleDialog
import com.example.compliment.ui.theme.RedDark
import com.example.compliment.ui.theme.WhiteBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun NotificationsScreen() {
    val viewModel = koinViewModel<NotificationsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.handleEvent(event)
        }
    )
}

@Composable
private fun NotificationsScreen(
    uiState: NotificationsUiState,
    onEvent: (NotificationsEvent) -> Unit
) {
    val schedules = rememberUpdatedState(uiState.schedules)

    Log.d("RecompositionTracker", "NotificationsScreen recomposed")
    val context = LocalContext.current

    val launcherToSetting = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val isGranted = checkPermission(context)
        Log.i("PERMISSION", "launcher granted $isGranted")
        onEvent(NotificationsEvent.PermissionResult(isGranted))
    }

    LaunchedEffect(key1 = true) {
        delay(500)
        val isGranted = checkPermission(context)
        onEvent(NotificationsEvent.PermissionResult(isGranted))
        Log.i("PERMISSION", "LaunchedEffect $isGranted")
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onEvent(NotificationsEvent.SaveSchedules)
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(NotificationsEvent.ShowAddScheduleDialog(true))
                },
                modifier = Modifier
                    .padding(bottom = 78.dp, end = 8.dp)
                    .size(84.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(painterResource(R.drawable.icon_cross), contentDescription = "Add Time")
            }
        },
        contentColor = RedDark,
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 74.dp)
        ) {
            Text(
                text = stringResource(R.string.title_reminders),
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            if (schedules.value.isEmpty()) {
                EmptyContentBox(paddingValues)
            } else {
                LazyColumnContent(schedules.value, onEvent = { event ->
                   onEvent(event)
                })
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (uiState.showScheduleDialog) {
            ScheduleDialog(
                existingData = uiState.currentScheduleData,
                currentSelectedDays = uiState.selectedDays,
                onTimeSelected = { hour, minute, days ->
                    val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

                    if (uiState.currentScheduleData == null) {
                        onEvent(NotificationsEvent.CreateSchedule(time, days))
                    } else {
                        val updatedSchedule =
                            uiState.currentScheduleData.copy(time = time, daysOfWeek = days)
                        onEvent(
                            NotificationsEvent.EditSchedule(
                                uiState.currentScheduleData,
                                updatedSchedule
                            )
                        )
                    }
                    onEvent(NotificationsEvent.ShowAddScheduleDialog(false))
                },
                onDismiss = { onEvent(NotificationsEvent.ShowAddScheduleDialog(false)) }
            )
        }

        if (uiState.showPermissionDialog) {
            PermissionDeniedDialog(
                context,
                launcherToSetting,
                onDismiss = {
                    onEvent(NotificationsEvent.ShowPermissionDialog(false))
                })
        }
    }
}

@Composable
fun LazyColumnContent(schedules: ImmutableList<NotificationScheduleWithFlow>, onEvent: (NotificationsEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        items(schedules, key = { it.time }) { schedule ->
            Log.d("RecompositionTracker", "LazyColumn items")

            val daysText = getTextDays(daysOfWeek = schedule.daysOfWeek)

            ScheduleItem(
                schedule = schedule,
                daysText = daysText,
                onCheckedChange = { isChecked ->
                    if (isChecked) onEvent(
                        NotificationsEvent.EnableSchedule(schedule.time, schedule.daysOfWeek)
                    )
                    else onEvent(NotificationsEvent.DisableSchedule(schedule.time, schedule.daysOfWeek))
                },
                onDelete = {
                    onEvent(NotificationsEvent.DeleteSchedule(schedule.time, schedule.daysOfWeek))
                },
                onEdit = {
                    onEvent(NotificationsEvent.ShowAddScheduleDialog(true, schedule))
                }
            )
        }
        item {
            Spacer(
                Modifier
                    .height(120.dp)
            )
        }
    }
}

@Composable
fun EmptyContentBox(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.empty_reminders),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

private fun checkPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}

@Composable
fun PermissionDeniedDialog(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onDismiss: () -> Unit
) {
    val openSettings: () -> Unit = {
        openAppSettings(context, launcher)
        onDismiss()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.permission_needed),
                color = MaterialTheme.colorScheme.onSecondary
            )
        },
        text = {
            Text(stringResource(R.string.text_permission_notifications))
        },
        confirmButton = {
            Button(onClick = openSettings) {
                Text(
                    text = stringResource(R.string.go_settings),
                    color = WhiteBackground
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = WhiteBackground
                )
            }
        }
    )
}

fun openAppSettings(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    launcher.launch(intent)
}

@Composable
fun getTextDays(daysOfWeek: ImmutableSet<DayOfWeek>): String{
    return if (daysOfWeek.size == 7) {
        stringResource(id = R.string.every_day)
    } else {
        daysOfWeek.joinToString(", ") {
            it.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    NotificationsScreen()
}

