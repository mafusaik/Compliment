package com.example.compliment.data.repositories

import android.util.Log
import com.example.compliment.data.database.NotificationDao
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.models.NotificationScheduleWithFlow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek


class NotificationRepositoryImpl(private val notificationDao: NotificationDao) : NotificationRepository {

    override suspend fun addSchedule(schedule: NotificationSchedule):Long {
       return notificationDao.insert(schedule)
    }

    //если обновлять в бд, то не работает нормально
//    override fun getSchedules(): Flow<ImmutableList<NotificationScheduleWithFlow>> {
//        return notificationDao
//            .getAllSchedules()
//            .map { schedules ->
//                schedules.map {schedule ->
//                NotificationScheduleWithFlow(
//                    time = schedule.time,
//                    daysOfWeek = schedule.daysOfWeek,
//                    isActive = notificationDao.observeIsActive(schedule.time)
//                )
//            }.toImmutableList()
//            }
//    }

    override fun getSchedules(): ImmutableList<NotificationScheduleWithFlow> {
        return notificationDao
            .getAllSchedules()
            .map {schedule ->
                Log.i("UPDATE_SCHEDULE", "get ${schedule.daysOfWeek}")
                NotificationScheduleWithFlow(
                    time = schedule.time,
                    daysOfWeek = schedule.daysOfWeek,
                    isActive = getFlowIsActive(schedule.time)
                )
            }.toImmutableList()
    }

    override fun getFlowIsActive(time: String): Flow<Boolean> {
       return notificationDao.observeIsActive(time)
    }

    override suspend fun updateScheduleDays(time: String, days: ImmutableSet<DayOfWeek>){
        notificationDao.updateDays(time, days)
    }

    override suspend fun updateSchedules(schedules: List<NotificationSchedule>){
        notificationDao.updateAll(schedules)
    }

    override suspend fun deleteSchedule(time: String){
        notificationDao.delete(time)
    }

    override suspend fun updateScheduleState(time: String, isActive: Boolean){
        notificationDao.updateIsActive(time, isActive)
    }

    override suspend fun getNotificationSchedule(time: String): NotificationSchedule? {
       return notificationDao.getNotificationSchedule(time)
    }
}