package com.example.compliment.data.database

import androidx.compose.runtime.Immutable
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.compliment.data.model.NotificationSchedule
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Dao
@Immutable
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: NotificationSchedule):Long

    @Query("SELECT * FROM notification_schedule")
    fun getAllSchedules(): List<NotificationSchedule>

    @Query("UPDATE notification_schedule SET daysOfWeek = :days WHERE time = :time")
    suspend fun updateDays(time: String, days: ImmutableSet<DayOfWeek>)

    @Update
    suspend fun updateAll(schedules: List<NotificationSchedule>)

    @Query("DELETE FROM notification_schedule WHERE time = :time")
    suspend fun delete(time: String)

    @Query("SELECT isActive FROM notification_schedule WHERE time = :time")
    fun observeIsActive(time: String): Flow<Boolean>

    @Query("UPDATE notification_schedule SET isActive = :isActive WHERE time = :time")
    suspend fun updateIsActive(time: String, isActive: Boolean)

    @Query("SELECT * FROM notification_schedule WHERE time = :time")
    suspend fun getNotificationSchedule(time: String): NotificationSchedule?
}