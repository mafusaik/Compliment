package com.example.compliment.models

import androidx.compose.runtime.Immutable
import java.util.Locale

@Immutable
data class SettingsUiState(
    val isDarkThemeEnabled: Boolean = false,
    val isExactTimeEnabled: Boolean = false,
    val isForWomen: Boolean = true,
    val showPermissionDialog: Boolean = false,
    val selectedLanguage: String = Locale.getDefault().language,
    val restartRequired: Boolean = false
)