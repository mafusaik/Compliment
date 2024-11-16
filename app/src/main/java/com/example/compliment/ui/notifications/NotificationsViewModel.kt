package com.example.compliment.ui.notifications

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.data.repositories.NotificationRepository
import com.example.compliment.data.sharedprefs.PrefsManager
import com.example.compliment.workers.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationsViewModel(
    private val repository: NotificationRepository
): ViewModel() {

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
            repository.getSchedules()
                .flowOn(Dispatchers.IO)
                .map {
                    _schedules.emit(it.toSet())
                }
                .launchIn(viewModelScope)
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

    fun saveSelectedDays(days: Set<DayOfWeek>){
     _selectedDays.value = days
    }

    fun deleteTimeSchedule(schedule: NotificationSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSchedule(schedule)
        }

    }

    fun updateScheduleState(schedule: NotificationSchedule, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSchedule = NotificationSchedule(schedule.time, schedule.daysOfWeek, isActive)
            repository.updateSchedule(newSchedule)
        }

    }

    fun addSchedule(time:String, days: Set<DayOfWeek>, isActive: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = NotificationSchedule(time, days, isActive)
            repository.addSchedule(schedule)
        }
    }

    fun startNotification(context: Context, schedule: NotificationSchedule) {
        Log.i("NOTIFICATIONS", "scheduleNotifications $schedule")
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("notification")

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
    }

    private fun cancelAllNotification(context: Context, schedules: Set<NotificationSchedule>) {
       schedules.forEach {
           cancelNotification(context, it.time)
       }
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis - System.currentTimeMillis()
    }
}