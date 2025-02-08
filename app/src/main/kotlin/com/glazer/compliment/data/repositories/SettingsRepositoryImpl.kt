package com.glazer.compliment.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SettingsRepositoryImpl : SettingsRepository {

    private val isExactTimeFlow = MutableStateFlow<Boolean?>(null)
    private val isDarkThemeFlow = MutableStateFlow<Boolean?>(null)

//    private val _isRecreate = MutableStateFlow(false)
//    val isRecreate: StateFlow<Boolean> = _isRecreate
    private var isRecreate = false

    override fun getIsExactTimeFlow(): StateFlow<Boolean?> {
        return isExactTimeFlow
    }

    override fun setIsExactTime(value: Boolean?) {
        isExactTimeFlow.value = value
    }

    override fun getIsDarkThemeFlow(): StateFlow<Boolean?> {
        return isDarkThemeFlow
    }

    override fun setIsDarkTheme(value: Boolean?) {
        isDarkThemeFlow.value = value
    }

    override fun setIsRecreate(value: Boolean) {
        isRecreate = value
    }

    override fun getIsRecreate(): Boolean {
        return isRecreate
    }
}