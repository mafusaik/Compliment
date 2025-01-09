package com.example.compliment.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compliment.data.repositories.ComplimentsRepository
import com.example.compliment.data.repositories.SettingsRepository
import com.example.compliment.data.sharedprefs.PrefsManager
import com.example.compliment.models.SettingsEvent
import com.example.compliment.models.SettingsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val complimentRepository: ComplimentsRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        Log.i("SettingsViewModel", "init")
        loadSavedSettings()
    }

    private fun loadSavedSettings() {
      _uiState.update { it.copy(
          isDarkThemeEnabled = prefsManager.isDarkTheme,
          isExactTimeEnabled = prefsManager.isExactTime,
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
            is SettingsEvent.ToggleForWomen -> {
                prefsManager.isForWomen = event.isForWomen
                _uiState.update { it.copy(isForWomen = event.isForWomen) }
            }
            is SettingsEvent.SelectLanguage -> {
                Log.i("SETTINGS", "SettingsEvent.SelectLanguage ${event.language}")
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
        Log.i("SETTINGS", "checkPermission hasPermission $hasPermission")
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