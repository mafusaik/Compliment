package com.example.compliment.data.repositories

import com.example.compliment.data.model.NotificationSchedule
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun addSchedule(schedule: NotificationSchedule)

    fun getSchedules(): Flow<List<NotificationSchedule>>

    suspend fun updateSchedule(schedule: NotificationSchedule)

    suspend fun updateSchedules(schedules: List<NotificationSchedule>)

    suspend fun deleteSchedule(schedule: NotificationSchedule)
}