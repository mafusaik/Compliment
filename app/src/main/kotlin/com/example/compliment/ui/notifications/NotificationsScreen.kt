package com.example.compliment.ui.notifications

import android.Manifest
import android.app.Activity
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.compliment.R
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.models.NotificationsEvent
import com.example.compliment.models.NotificationsUiState
import com.example.compliment.ui.notifications.dialog.ScheduleDialog
import com.example.compliment.ui.theme.Black
import com.example.compliment.ui.theme.RedDark
import com.example.compliment.ui.theme.WhiteBackground
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun NotificationsScreen() {
    val viewModel = koinViewModel<NotificationsViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   // val schedules by viewModel.localSchedules.collectAsStateWithLifecycle()

    NotificationsScreen(
        uiState = uiState,
      //  schedules = schedules,
        onEvent = { event ->
            viewModel.handleEvent(event)
        }
    )
}

@Composable
private fun NotificationsScreen(
    uiState: NotificationsUiState,
  //  schedules: List<NotificationSchedule>,
    onEvent: (NotificationsEvent) -> Unit
) {
//     val schedules by remember { derivedStateOf { uiState.schedules } }
    val schedules = uiState.schedules
    Log.d("RecompositionTracker", "NotificationsScreen recomposed ${schedules}")
    val context = LocalContext.current

    val launcherRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onEvent(NotificationsEvent.PermissionResult(isGranted))
    }

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

            if (schedules.isEmpty()) {
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
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    items(schedules.toList(), key = { it.time.hashCode() }) { schedule ->
                        Log.d("RecompositionTracker", "LazyColumn items")
//                        val isActive by rememberUpdatedState(schedule.isActive)
                        var isActive by remember { mutableStateOf(schedule.isActive) }

                        ScheduleItem(
                            schedule = schedule.copy(isActive = isActive),
                            onCheckedChange = { isChecked ->
                                isActive = isChecked
                                if (uiState.isPermissionGranted) {
                                    if (isChecked) onEvent(
                                        NotificationsEvent.EnableSchedule(schedule)
                                    )
                                    else onEvent(NotificationsEvent.DisableSchedule(schedule))
                                } else {
                                    checkAndRequestPermission(
                                        context,
                                        launcherRequest,
                                    ) { onEvent(NotificationsEvent.ShowPermissionDialog(true)) }
                                }
                            },
                            onDelete = {
                                onEvent(NotificationsEvent.DeleteSchedule(schedule))
                            },
                            onEdit = {
                                onEvent(NotificationsEvent.ShowAddScheduleDialog(true, schedule))
                            }
                        )

                        if (schedule == schedules.toList().last()) {
                            Spacer(Modifier.height(120.dp))
                        }
                    }
                }
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

private fun checkAndRequestPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onShow: (Boolean) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity, Manifest.permission.POST_NOTIFICATIONS
        )

        if (shouldShowRationale) {
            onShow(true)
        } else {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
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
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.onSecondary
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


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    NotificationsScreen()
}

