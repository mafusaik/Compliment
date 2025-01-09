package com.example.compliment.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.compliment.data.model.NotificationSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: NotificationSchedule)

    @Query("SELECT * FROM notification_schedule")
    fun getAllSchedules(): Flow<List<NotificationSchedule>>

    @Update
    suspend fun update(schedule: NotificationSchedule)

    @Update
    suspend fun updateAll(schedules: List<NotificationSchedule>)

    @Delete
    suspend fun delete(schedule: NotificationSchedule)
}