package com.example.compliment.models
sealed class SettingsEvent {
    data class ToggleDarkTheme(val isDark: Boolean) : SettingsEvent()
    data class ToggleExactTime(val hasPermission: Boolean, val isEnable: Boolean) : SettingsEvent()
    data class ToggleForWomen(val isForWomen: Boolean) : SettingsEvent()

    data class ShowPermissionDialog(val isShow: Boolean) : SettingsEvent()
    data class SelectLanguage(val language: String) : SettingsEvent()
    data object ResetRestartFlag : SettingsEvent()
}