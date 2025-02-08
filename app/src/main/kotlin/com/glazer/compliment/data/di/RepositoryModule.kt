package com.glazer.compliment.data.di

import com.glazer.compliment.alarm.AlarmScheduler
import com.glazer.compliment.alarm.AndroidAlarmScheduler
import com.glazer.compliment.data.repositories.ComplimentsRepository
import com.glazer.compliment.data.repositories.ComplimentsRepositoryImpl
import com.glazer.compliment.data.repositories.NotificationRepository
import com.glazer.compliment.data.repositories.NotificationRepositoryImpl
import com.glazer.compliment.data.repositories.SettingsRepository
import com.glazer.compliment.data.repositories.SettingsRepositoryImpl
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
