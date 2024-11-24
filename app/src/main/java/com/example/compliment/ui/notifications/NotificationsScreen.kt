package com.example.compliment.ui.notifications

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.extensions.showToast
import com.example.compliment.models.ScheduleItem
import com.example.compliment.ui.notifications.dialog.ScheduleDialog
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun NotificationsScreen() {
    val context = LocalContext.current
    val viewModel = koinViewModel<NotificationsViewModel>()

   // val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    val selectedDays by viewModel.selectedDays.collectAsState()
    val isPermissionGranted by viewModel.isPermissionGranted.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var currentScheduleData by remember { mutableStateOf<NotificationSchedule?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i("PERMISSION", "granted")
            viewModel.onNotificationPermissionGranted()
        } else {
            Log.i("PERMISSION", "denied")
            viewModel.onNotificationPermissionDenied()
        }
    }

    LaunchedEffect(schedules) {
        schedules.forEach { schedule ->
            if (schedule.isActive) {
                viewModel.startNotification(context, schedule)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentScheduleData = null
                showScheduleDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Time")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Selected Notification Times",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "This is empty for now, create a new reminder",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(schedules.toList(), key = { it.time }) { schedule ->
                        val isActive = schedule.isActive
                        ScheduleItem(
                            schedule = schedule,
                            isChecked = isActive,
                            onCheckedChange = { isChecked ->
                                if (isPermissionGranted){
                                    viewModel.updateScheduleState(schedule, isChecked)
                                    if (isChecked) viewModel.startNotification(context, schedule)
                                    else viewModel.cancelNotification(context, schedule.time)
                                }else{
                                    checkAndRequestPermission(context, launcher) { showPermissionDialog = it }
                                }

                            },
                            onDelete = {
                                viewModel.deleteTimeSchedule(schedule)
                                viewModel.cancelNotification(context, schedule.time)
                            },
                            onEdit = {
                                currentScheduleData = schedule
                                showScheduleDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showScheduleDialog) {
            if (isPermissionGranted)
                ScheduleDialog(
                    existingData = currentScheduleData,
                    currentSelectedDays = selectedDays,
                    onTimeSelected = { hour, minute, days ->
                        val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
                        viewModel.saveSelectedDays(days)
                        currentScheduleData?.let {
                            viewModel.deleteTimeSchedule(it)
                        }
                        viewModel.addSchedule(time, days, true)
                        showScheduleDialog = false
                    },
                    onDismiss = { showScheduleDialog = false }
                )
            else checkAndRequestPermission(context, launcher) { showPermissionDialog = it }
        }

        if (showPermissionDialog) {
            PermissionDeniedDialog(context, onDismiss = {
                showPermissionDialog = false
                showScheduleDialog = false
            })
        }
    }

    LaunchedEffect(lifecycleState) {
        Log.i("PERMISSION", "LaunchedEffect")
        if (lifecycleState == Lifecycle.State.STARTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                val isGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (isGranted) {
                    viewModel.onNotificationPermissionGranted()
                } else {
                    viewModel.onNotificationPermissionDenied()
                }
            } else viewModel.onNotificationPermissionGranted()


// Для воркменеджера надо оформить эту проверку (если с будильником не получится)
            if (!isIgnoringBatteryOptimizations(context)) {
                context.showToast("For stable operation of notifications, it is necessary to disable battery optimization.")
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                context.startActivity(intent)
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                if (!alarmManager.canScheduleExactAlarms()) {
//                    Intent().also { intent ->
//                        intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
//                        context.startActivity(intent)
//                    }
//                }
//            }
        }
    }
}

private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
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
            context.showToast("We need permission to send notifications. Please grant it.")
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onShow(true)
        }
    }
}

@Composable
fun PermissionDeniedDialog(context: Context, onDismiss: () -> Unit) {
    val openSettings: () -> Unit = {
        openAppSettings(context)
        onDismiss()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Permission Needed")
        },
        text = {
            Text("This permission is required for notifications. Please enable it in the app settings.")
        },
        confirmButton = {
            Button(onClick = openSettings) {
                Text("Go to Settings")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
    }
    context.startActivity(intent)
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    NotificationsScreen()
}

