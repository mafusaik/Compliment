package com.glazer.compliment.data.repositories

import kotlinx.coroutines.flow.StateFlow


interface SettingsRepository {
    fun getIsExactTimeFlow(): StateFlow<Boolean?>

    fun setIsExactTime(value: Boolean?)

    fun getIsDarkThemeFlow(): StateFlow<Boolean?>

    fun setIsDarkTheme(value: Boolean?)

    fun setIsRecreate(value: Boolean)

    fun getIsRecreate(): Boolean
}