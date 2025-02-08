package com.glazer.compliment.data.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.glazer.compliment.ui.notifications.NotificationsViewModel
import com.glazer.compliment.ui.home.HomeViewModel
import com.glazer.compliment.ui.settings.SettingsViewModel
import com.glazer.compliment.ui.bottommenu.MainScreenViewModel


val viewModelModule = module {
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainScreenViewModel)
}
