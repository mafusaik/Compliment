package com.glazer.compliment.models

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsUiState(
    val isDarkThemeEnabled: Boolean = false,
    val isExactTimeEnabled: Boolean = false,
    val selectedGender: String = "",
    val showPermissionDialog: Boolean = false,
    val selectedLanguage: String = "en",
    val restartRequired: Boolean = false
)