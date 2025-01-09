package com.example.compliment.ui.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compliment.alarm.AlarmScheduler
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.data.repositories.NotificationRepository
import com.example.compliment.data.repositories.SettingsRepository
import com.example.compliment.models.NotificationsEvent
import com.example.compliment.models.NotificationsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class NotificationsViewModel(
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = this._uiState.asStateFlow()

//    private val _localSchedules = MutableStateFlow<List<NotificationSchedule>>(emptyList())
//    val localSchedules: StateFlow<List<NotificationSchedule>> = _localSchedules.asStateFlow()

    init {
        loadSavedSettings()
        listenIsExactTimeFlow()
    }

    fun handleEvent(event: NotificationsEvent) {
        when (event) {
            is NotificationsEvent.EnableSchedule -> {
               // updateScheduleState(event.schedule, true)
                startSchedule(event.schedule)
            }

            is NotificationsEvent.DisableSchedule -> {
              //  updateScheduleState(event.schedule, false)
                cancelSchedule(event.schedule)
            }

            is NotificationsEvent.DeleteSchedule -> {
                deleteTimeSchedule(event.schedule)
            }

            is NotificationsEvent.CreateSchedule -> {
                val isActive = this._uiState.value.isPermissionGranted
                addSchedule(event.time, event.days, isActive)
                this._uiState.update { it.copy(selectedDays = event.days) }
            }

            is NotificationsEvent.EditSchedule -> {
                if (event.schedule != null && event.schedule.time != event.oldSchedule.time) {
                    deleteTimeSchedule(event.oldSchedule)
                    addSchedule(
                        event.schedule.time,
                        event.schedule.daysOfWeek,
                        event.schedule.isActive
                    )
                    this._uiState.update { it.copy(selectedDays = event.schedule.daysOfWeek) }
                } else if (event.schedule != null) {
                    updateScheduleState(event.schedule, event.schedule.isActive)
                }
            }

            is NotificationsEvent.SaveSchedules -> {
                saveChanges()
            }

            is NotificationsEvent.ShowAddScheduleDialog -> {
                if (event.isShow)
                    this._uiState.update {
                        it.copy(
                            showScheduleDialog = true,
                            currentScheduleData = event.currentSchedule
                        )
                    }
                else this._uiState.update {
                    it.copy(
                        showScheduleDialog = false,
                        showPermissionDialog = false,
                        currentScheduleData = null
                    )
                }
            }

            is NotificationsEvent.PermissionResult -> {
                this._uiState.update { it.copy(isPermissionGranted = event.isGranted) }
                if (!event.isGranted) {
                    disableAllNotifications()
                }
            }

            is NotificationsEvent.ShowPermissionDialog -> {
                this._uiState.update { it.copy(showPermissionDialog = event.isShow) }
            }
        }
    }

    private fun listenIsExactTimeFlow() {
        viewModelScope.launch {
            settingsRepository.getIsExactTimeFlow().collectLatest {
                Log.i("SETTINGS", "change isExact $it")
//                localSchedules.value.forEach { schedule ->
//                    cancelSchedule(schedule)
//                    startSchedule(schedule)
//                }
                this@NotificationsViewModel._uiState.value.schedules.forEach { schedule->
                    cancelSchedule(schedule)
                    startSchedule(schedule)
                }
            }
        }
    }

    private fun loadSavedSettings() {
        viewModelScope.launch {
            notificationRepository.getSchedules()
                .map { it.toSet() }
                .distinctUntilChanged { old, new -> old == new }
                .flowOn(Dispatchers.IO)
                .collectLatest {
//                    _localSchedules.emit(it.toList())
                    this@NotificationsViewModel._uiState.update { state ->
                        state.copy(schedules = it.toSet())
                    }
                }
        }
    }


    private fun disableAllNotifications() {
//        localSchedules.value.forEach { schedule ->
////            cancelSchedule(schedule)
////            startSchedule(schedule)
//        }
        this._uiState.value.schedules.forEach {
            updateScheduleState(it, false)
        }
    }


    private fun deleteTimeSchedule(schedule: NotificationSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            cancelSchedule(schedule)
            notificationRepository.deleteSchedule(schedule)
        }
    }


    private fun updateScheduleState(schedule: NotificationSchedule, isActive: Boolean) {
//        _localSchedules.update { currentList ->
//            currentList.map {
//                if (it.time == schedule.time) schedule.copy(isActive = isActive)
//                else it
//            }
//        }
        viewModelScope.launch(Dispatchers.IO) {
            val newSchedule = schedule.copy(isActive = isActive)
            notificationRepository.updateSchedule(newSchedule)
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
        //    notificationRepository.updateSchedules(_localSchedules.value)
        }
    }

    private fun addSchedule(time: String, days: Set<DayOfWeek>, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val schedule = NotificationSchedule(time, days, isActive)
            notificationRepository.addSchedule(schedule)
            startSchedule(schedule)
        }
    }

    private fun startSchedule(schedule: NotificationSchedule) {
        Log.i("NOTIFICATIONS", "scheduleNotifications $schedule")
        viewModelScope.launch {
            alarmScheduler.createSchedule(schedule)
        }
    }

    private fun cancelSchedule(schedule: NotificationSchedule) {
        alarmScheduler.cancel(schedule)
    }

//    fun startSchedule(context: Context, schedule: NotificationSchedule) {
//        Log.i("NOTIFICATIONS", "scheduleNotifications $schedule")
//        viewModelScope.launch {
//            alarmScheduler.schedule(schedule)
//        }


//        val workManager = WorkManager.getInstance(context)
//        cancelNotification(context, schedule.time)
//
//            val (hour, minute) = schedule.time.split(":").map { it.toInt() }
//            val daysAsString = schedule.daysOfWeek.map { it.name }.toSet()
//            val delay = calculateInitialDelay(hour, minute)
//
//            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
//                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
//                .setInputData(workDataOf("days" to daysAsString.joinToString(",")))
//                .addTag("notification")
//                .build()
//
//            workManager.enqueueUniquePeriodicWork(
//                "notification_${schedule.time}",
//                ExistingPeriodicWorkPolicy.UPDATE,
//                workRequest
//            )
//    }

//    fun cancelSchedule(context: Context, schedule: NotificationSchedule) {
//        val workManager = WorkManager.getInstance(context)
//        workManager.cancelUniqueWork("notification_$time")
//        Log.i("NOTIFICATIONS", "Notification cancelled for $time")
//    }

//    private fun cancelAllNotification(schedules: Set<NotificationSchedule>) {
//        schedules.forEach {
//            cancelSchedule(it)
//        }
//    }


}