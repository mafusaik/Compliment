package com.glazer.compliment.ui.notifications.dialog

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.glazer.compliment.R
import com.glazer.compliment.ui.notifications.openAppSettings

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
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    )
}