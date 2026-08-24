package com.glazer.compliment.ui.settings


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.glazer.compliment.R

@Composable
fun PermissionDialog(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onDismiss: () -> Unit,
) {
    val openSettings: () -> Unit = {
        openAlarmSettings(context, launcher)
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
        text = { Text(stringResource(R.string.text_permission_exact_alarm)) },
        confirmButton = {
            TextButton(onClick = openSettings) {
                Text(
                    text = stringResource(R.string.go_settings),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    )
}

private fun openAlarmSettings(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        launcher.launch(intent)
    }
}