package com.example.compliment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.compliment.data.model.NotificationSchedule

@Database(entities = [NotificationSchedule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class GeneralDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao

}