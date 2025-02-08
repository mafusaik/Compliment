package com.glazer.compliment.data.repositories

import androidx.compose.runtime.Immutable
import com.glazer.compliment.data.model.NotificationSchedule
import com.glazer.compliment.models.NotificationScheduleWithFlow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Immutable
interface NotificationRepository {
    suspend fun addSchedule(schedule: NotificationSchedule): Long

    fun getSchedules(): ImmutableList<NotificationScheduleWithFlow>

    suspend fun updateScheduleDays(time: String, days: ImmutableSet<DayOfWeek>)

    suspend fun updateSchedules(schedules: List<NotificationSchedule>)

    suspend fun deleteSchedule(time: String)

    suspend fun updateScheduleState(time: String, isActive: Boolean)

    fun getFlowIsActive(time: String): Flow<Boolean>

    suspend fun getNotificationSchedule(time: String): NotificationSchedule?
}