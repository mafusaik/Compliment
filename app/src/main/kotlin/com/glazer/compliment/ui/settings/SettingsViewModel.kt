package com.glazer.compliment.ui.settings

import androidx.lifecycle.ViewModel
import com.glazer.compliment.data.repositories.SettingsRepository
import com.glazer.compliment.data.sharedprefs.PrefsManager
import com.glazer.compliment.models.SettingsEvent
import com.glazer.compliment.models.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSavedSettings()
    }

    private fun loadSavedSettings() {
      _uiState.update { it.copy(
          isDarkThemeEnabled = prefsManager.isDarkTheme,
          isExactTimeEnabled = prefsManager.isExactTime,
          selectedGender = prefsManager.currentGender,
          showPermissionDialog = false,
          selectedLanguage = prefsManager.currentLanguage
      ) }
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleDarkTheme -> {
                prefsManager.isDarkTheme = event.isDark
                changeTheme(event.isDark)
                _uiState.update { it.copy(isDarkThemeEnabled = event.isDark) }
            }
            is SettingsEvent.ToggleExactTime -> {
                checkPermissionAndToggle(event.hasPermission, event.isEnable)
            }
            is SettingsEvent.SelectGender -> {
                prefsManager.currentGender = event.gender
                _uiState.update { it.copy(selectedGender = event.gender) }
            }
            is SettingsEvent.SelectLanguage -> {
                prefsManager.currentLanguage = event.language
                repository.setIsRecreate(true)
                _uiState.update { it.copy(selectedLanguage = event.language, restartRequired = true) }
            }
            is SettingsEvent.ShowPermissionDialog -> {
                _uiState.update { it.copy(showPermissionDialog = event.isShow) }
            }

            is SettingsEvent.ResetRestartFlag -> {
                _uiState.update { it.copy(restartRequired = false) }
            }
        }
    }

    private fun checkPermissionAndToggle(hasPermission: Boolean, isEnable: Boolean) {
        if (hasPermission && isEnable) {
            _uiState.update {
                val isExact = !it.isExactTimeEnabled
                prefsManager.isExactTime = isExact
                repository.setIsExactTime(isExact)
                it.copy(isExactTimeEnabled = isExact)
            }
        } else if (!hasPermission && isEnable){
            prefsManager.isExactTime = false
            repository.setIsExactTime(false)
            _uiState.update { it.copy(isExactTimeEnabled = false, showPermissionDialog = true) }
        } else {
            prefsManager.isExactTime = false
            repository.setIsExactTime(false)
            _uiState.update { it.copy(isExactTimeEnabled = false, showPermissionDialog = false) }
        }
    }

    private fun changeTheme(isDark: Boolean){
        repository.setIsDarkTheme(isDark)
    }

}