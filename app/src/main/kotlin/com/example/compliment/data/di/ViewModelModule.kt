package com.example.compliment.data.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.compliment.ui.notifications.NotificationsViewModel
import com.example.compliment.ui.home.HomeViewModel
import com.example.compliment.ui.settings.SettingsViewModel
import com.example.compliment.ui.bottommenu.MainScreenViewModel


val viewModelModule = module {
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainScreenViewModel)
}
