package com.example.compliment.data.di

import com.example.compliment.workers.NotificationWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module


val workerModule = module {
    workerOf(::NotificationWorker)
}
