package com.example.compliment.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.data.repositories.ComplimentsRepository
import com.example.compliment.data.repositories.NotificationRepository
import com.example.compliment.receivers.NotificationReceiver
import com.example.compliment.workers.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    private val complimentRepository: ComplimentsRepository
) : ViewModel() {

    private val _selectedDays = MutableStateFlow<Set<DayOfWeek>>(emptySet())
    val selectedDays: StateFlow<Set<DayOfWeek>> = _selectedDays

    private val _schedules = MutableStateFlow<Set<NotificationSchedule>>(emptySet())
    val schedules: StateFlow<Set<NotificationSchedule>> = _schedules

    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    init {
        loadSavedSettings()
    }


    private fun loadSavedSettings() {
        viewModelScope.launch {
            notificationRepository.getSchedules()
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    _schedules.emit(it.toSet())
                }
        }
    }


    fun onNotificationPermissionGranted() {
        _isPermissionGranted.value = true
    }

    fun onNotificationPermissionDenied() {
        _isPermissionGranted.value = false
        _schedules.value.forEach {
            updateScheduleState(it, false)
        }
    }

    fun saveSelectedDays(days: Set<DayOfWeek>) {
        _selectedDays.value = days
    }

    fun deleteTimeSchedule(schedule: NotificationSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.deleteSchedule(schedule)
        }

    }

    fun updateScheduleState(schedule: NotificationSchedule, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSchedule = NotificationSchedule(schedule.time, schedule.daysOfWeek, isActive)
            notificationRepository.updateSchedule(newSchedule)
        }

    }

    fun addSchedule(time: String, days: Set<DayOfWeek>, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = NotificationSchedule(time, days, isActive)
            notificationRepository.addSchedule(schedule)
        }
    }

    fun startNotification(context: Context, schedule: NotificationSchedule) {
        Log.i("NOTIFICATIONS", "scheduleNotifications $schedule")
        val workManager = WorkManager.getInstance(context)
        //workManager.cancelAllWorkByTag("notification")
        cancelNotification(context, schedule.time)

            val (hour, minute) = schedule.time.split(":").map { it.toInt() }
            val daysAsString = schedule.daysOfWeek.map { it.name }.toSet()
            val delay = calculateInitialDelay(hour, minute)

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("days" to daysAsString.joinToString(",")))
                .addTag("notification")
                .build()

            workManager.enqueueUniquePeriodicWork(
                "notification_${schedule.time}",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    fun cancelNotification(context: Context, time: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("notification_$time")
        Log.i("NOTIFICATIONS", "Notification cancelled for $time")
    }

    private fun cancelAllNotification(context: Context, schedules: Set<NotificationSchedule>) {
        schedules.forEach {
            cancelNotification(context, it.time)
        }
    }


    //-----ALARM MANAGER doesn't work------

//    fun startNotification(
//        context: Context,
//        schedule: NotificationSchedule,
//        alarmManager: AlarmManager
//    ) {
//        // Log.i("NOTIFICATIONS", "scheduleNotifications $schedule")
//        viewModelScope.launch {
//            cancelNotification(context, schedule, alarmManager)
//
//            val (hour, minute) = schedule.time.split(":").map { it.toInt() }
//
//            val randomCompliment = complimentRepository.getCompliment()
//
//            val intent = Intent("com.example.compliment.NOTIFY").apply {
//                setClass(context, NotificationReceiver::class.kotlin)
//                data = Uri.parse("scheme://time/${schedule.time}")
//                putExtra("schedule_time", schedule.time)
//                putExtra("days", schedule.daysOfWeek.joinToString(","))
//                //  putExtra("message", randomCompliment)
//            }
//
//            val uniqueId = (schedule.time + schedule.daysOfWeek).hashCode()
//
//            val pendingIntent = PendingIntent.getBroadcast(
//                context,
//                uniqueId,
//                intent,
//                PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val firstTriggerTime = calculateInitialDelay(hour, minute)
//
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                firstTriggerTime,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )
//            Log.i("NOTIFICATIONS", "Notification scheduled for ${schedule.time} every day")
//        }
//    }
//
//    fun cancelNotification(
//        context: Context,
//        schedule: NotificationSchedule,
//        alarmManager: AlarmManager
//    ) {
//        val intent = Intent("com.example.compliment.NOTIFY").apply {
//            setClass(context, NotificationReceiver::class.kotlin)
//            data = Uri.parse("scheme://time/${schedule.time}")
//            putExtra("schedule_time", schedule.time)
//            putExtra("days", schedule.daysOfWeek.joinToString(","))
//            //  putExtra("message", randomCompliment)
//        }
//        val uniqueId = (schedule.time + schedule.daysOfWeek).hashCode()
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            uniqueId,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        alarmManager.cancel(pendingIntent)
//        pendingIntent.cancel()
//        Log.i("NOTIFICATIONS", "Notification cancelled for ${schedule.time}")
//    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            //  timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        val now = System.currentTimeMillis()
        if (calendar.timeInMillis < now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis - now
    }
}