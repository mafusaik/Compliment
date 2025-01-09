package com.example.compliment.data.repositories

import com.example.compliment.data.database.NotificationDao
import com.example.compliment.data.model.NotificationSchedule
import kotlinx.coroutines.flow.Flow

class NotificationRepositoryImpl(private val notificationDao: NotificationDao) : NotificationRepository {

    override suspend fun addSchedule(schedule: NotificationSchedule) {
        notificationDao.insert(schedule)
    }

    override fun getSchedules(): Flow<List<NotificationSchedule>> {
        return notificationDao.getAllSchedules()
    }

    override suspend fun updateSchedule(schedule: NotificationSchedule){
        notificationDao.update(schedule)
    }

    override suspend fun updateSchedules(schedules: List<NotificationSchedule>){
        notificationDao.updateAll(schedules)
    }

    override suspend fun deleteSchedule(schedule: NotificationSchedule){
        notificationDao.delete(schedule)
    }

}