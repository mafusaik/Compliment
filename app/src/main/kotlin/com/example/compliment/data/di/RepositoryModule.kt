package com.example.compliment.data.di

import com.example.compliment.alarm.AlarmScheduler
import com.example.compliment.alarm.AndroidAlarmScheduler
import com.example.compliment.data.repositories.ComplimentsRepository
import com.example.compliment.data.repositories.ComplimentsRepositoryImpl
import com.example.compliment.data.repositories.NotificationRepository
import com.example.compliment.data.repositories.NotificationRepositoryImpl
import com.example.compliment.data.repositories.SettingsRepository
import com.example.compliment.data.repositories.SettingsRepositoryImpl
import org.koin.dsl.module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf


val repositoryModule = module {
    singleOf(::ComplimentsRepositoryImpl) {
        bind<ComplimentsRepository>()
    }

    singleOf(::NotificationRepositoryImpl) {
        bind<NotificationRepository>()
    }

    singleOf(::SettingsRepositoryImpl) {
        bind<SettingsRepository>()
    }

    singleOf(::AndroidAlarmScheduler) {
        bind<AlarmScheduler>()
    }
}
