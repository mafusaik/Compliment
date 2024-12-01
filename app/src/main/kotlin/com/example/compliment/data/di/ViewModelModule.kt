package com.example.compliment.data.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.compliment.ui.notifications.NotificationsViewModel
import com.example.compliment.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel


val viewModelModule = module {
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::HomeViewModel)
}
