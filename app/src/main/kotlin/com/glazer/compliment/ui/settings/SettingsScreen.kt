@file:OptIn(ExperimentalMaterial3Api::class)

package com.glazer.compliment.ui.settings

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glazer.compliment.R
import com.glazer.compliment.data.sharedprefs.PrefsManager
import com.glazer.compliment.extensions.langCodeToLang
import com.glazer.compliment.extensions.langToLangCode
import com.glazer.compliment.extensions.setAppLocale
import com.glazer.compliment.models.SettingsEvent
import com.glazer.compliment.models.SettingsUiState
import com.glazer.compliment.ui.elements.CustomSwitch
import com.glazer.compliment.utils.Constants
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel = koinViewModel<SettingsViewModel>(
        parameters = { parametersOf(PrefsManager(context)) }
    )
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onEvent = { event -> viewModel.handleEvent(event) }
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit
) {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val startGender =
        if (uiState.selectedGender == Constants.GENDER_WOMEN) stringResource(R.string.for_women)
        else stringResource(R.string.for_men)

    val selectedLanguage by remember { mutableStateOf(uiState.selectedLanguage) }
    val startLanguage = selectedLanguage.langCodeToLang(context)

    val genders = listOf(stringResource(R.string.for_women), stringResource(R.string.for_men))
    val languages =
        listOf(stringResource(R.string.lang_english), stringResource(R.string.lang_russian))

    val labelLang = stringResource(R.string.language)
    val labelGender = stringResource(R.string.gender)

    val launcherToSetting = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val isEnable = isHasPermission(alarmManager)
        onEvent(SettingsEvent.ToggleExactTime(isEnable, isEnable))
    }

    Log.d("RecompositionTracker", "SettingsScreen recomposition")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 74.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.title_settings),
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        SettingsItem(
            stringResource(R.string.enable_dark_theme),
            uiState.isDarkThemeEnabled
        ) {
            onEvent(SettingsEvent.ToggleDarkTheme(it))
        }

        SettingsItem(
            stringResource(R.string.enable_exact_notification_time),
            uiState.isExactTimeEnabled
        ) { isChecked ->
            onEvent(
                SettingsEvent.ToggleExactTime(
                    isHasPermission(alarmManager),
                    isChecked
                )
            )
        }

        ValueSelector(labelGender, startGender, genders) { gender ->
            val value = if (gender == context.getString(R.string.for_women)) Constants.GENDER_WOMEN
            else Constants.GENDER_MEN
            onEvent(SettingsEvent.SelectGender(value))
        }

        ValueSelector(labelLang, startLanguage, languages) { language ->
            val languageCode = language.langToLangCode(context)
            context.setAppLocale(languageCode)
            onEvent(SettingsEvent.SelectLanguage(languageCode))
        }

        if (uiState.showPermissionDialog) {
            PermissionDialog(
                context,
                launcherToSetting,
                onDismiss = {
                    onEvent(SettingsEvent.ShowPermissionDialog(false))
                })
        }

        if (uiState.restartRequired) {
            (context as? Activity)?.recreate()
            onEvent(SettingsEvent.ResetRestartFlag)
        }
    }
}

fun isHasPermission(alarmManager: AlarmManager): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}

@Composable
fun SettingsItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        CustomSwitch(isChecked) {
            onCheckedChange(it)
        }
    }
}

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

@Composable
fun ValueSelector(
    label: String,
    startValue: String,
    listValues: List<String>,
    onValueChange: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = it }
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val focusManager = LocalFocusManager.current

        LaunchedEffect(isDropdownExpanded) {
            if (!isDropdownExpanded) {
                focusManager.clearFocus(true)
            }
        }

        OutlinedTextField(
            value = startValue,
            onValueChange = { },
            label = {
                Text(
                    text = label,
                    fontSize = 16.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                focusedTextColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(8.dp),
            readOnly = true,
            interactionSource = interactionSource,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
            },
            textStyle = TextStyle.Default.copy(fontSize = 20.sp),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = {
                isDropdownExpanded = false
                focusManager.clearFocus(true)
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            listValues.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = value,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    },
                    onClick = {
                        if (value != startValue) {
                            onValueChange(value)
                        }
                        isDropdownExpanded = false
                        focusManager.clearFocus()
                    }
                )
            }
        }
    }
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


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}