package com.glazer.compliment.models

import androidx.compose.runtime.Immutable

@Immutable
sealed class SettingsEvent {
    @Immutable
    data class ToggleDarkTheme(val isDark: Boolean) : SettingsEvent()
    @Immutable
    data class ToggleExactTime(val hasPermission: Boolean, val isEnable: Boolean) : SettingsEvent()
//    @Immutable
//    data class ToggleForWomen(val isForWomen: Boolean) : SettingsEvent()
    @Immutable
    data class SelectGender(val gender: String) : SettingsEvent()
    @Immutable
    data class ShowPermissionDialog(val isShow: Boolean) : SettingsEvent()
    @Immutable
    data class SelectLanguage(val language: String) : SettingsEvent()
    data object ResetRestartFlag : SettingsEvent()
}