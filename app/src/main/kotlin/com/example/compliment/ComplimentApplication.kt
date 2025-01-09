package com.example.compliment

import android.app.Application
import com.example.compliment.data.di.dataModule
import com.example.compliment.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin


class ComplimentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ComplimentApplication)
            modules(viewModelModule, dataModule)
            workManagerFactory()
        }
    }
}