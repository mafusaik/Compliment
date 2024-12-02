package com.example.compliment.data.di

import androidx.room.Room
import com.example.compliment.data.database.GeneralDatabase
import org.koin.dsl.module

internal val roomModule = module {
    single {
        Room.databaseBuilder(
            get(),
            GeneralDatabase::class.java,
            "database"
        )
            .build()
    }

    single { get<GeneralDatabase>().notificationDao() }
}