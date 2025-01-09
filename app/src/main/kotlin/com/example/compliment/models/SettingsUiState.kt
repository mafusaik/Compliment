package com.example.compliment.models

import java.util.Locale

data class SettingsUiState(
    val isDarkThemeEnabled: Boolean = false,
    val isExactTimeEnabled: Boolean = false,
    val isForWomen: Boolean = true,
    val showPermissionDialog: Boolean = false,
    val selectedLanguage: String = Locale.getDefault().language,
    val restartRequired: Boolean = false
)