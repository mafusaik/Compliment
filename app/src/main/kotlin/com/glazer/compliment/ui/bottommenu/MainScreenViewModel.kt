package com.glazer.compliment.ui.bottommenu

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glazer.compliment.data.repositories.ComplimentsRepository
import com.glazer.compliment.data.repositories.SettingsRepository
import com.glazer.compliment.data.sharedprefs.PrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val settingsRepository: SettingsRepository,
    private val complimentRepository: ComplimentsRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {
    private val _isDarkTheme = MutableStateFlow(prefsManager.isDarkTheme)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        Log.i("MainScreenViewModel", "init")
        listenThemeFlow()
    }

    fun checkRecreate(newContext: Context) {
        viewModelScope.launch {
           // if (getIsRecreate()){
                complimentRepository.changeComplimentLang(newContext)
                setIsRecreate(false)
         //   }
        }
    }

    private fun listenThemeFlow() {
        viewModelScope.launch {
            settingsRepository.getIsDarkThemeFlow().collectLatest {
                if (it != null) _isDarkTheme.emit(it)
            }
        }
    }

    private fun setIsRecreate(value: Boolean) {
        settingsRepository.setIsRecreate(value)
    }

    private fun getIsRecreate(): Boolean {
        return settingsRepository.getIsRecreate()
    }
}