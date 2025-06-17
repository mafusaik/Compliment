package com.glazer.compliment

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.glazer.compliment.data.di.dataModule
import com.glazer.compliment.data.di.viewModelModule
import com.glazer.compliment.utils.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin


class ComplimentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startKoin {
            androidContext(this@ComplimentApplication)
            modules(viewModelModule, dataModule)
            workManagerFactory()
        }
    }

    private fun createNotificationChannel() {
        val channelReminders = NotificationChannel(
            Constants.CHANNEL_ID,
            "Reminders",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Reminders"
            enableVibration(true)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channelReminders)
    }
}