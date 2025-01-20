package com.example.compliment.ui.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compliment.alarm.AlarmScheduler
import com.example.compliment.data.model.NotificationSchedule
import com.example.compliment.data.repositories.NotificationRepository
import com.example.compliment.data.repositories.SettingsRepository
import com.example.compliment.models.NotificationScheduleWithFlow
import com.example.compliment.models.NotificationsEvent
import com.example.compliment.models.NotificationsUiState
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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


    init {
        loadSavedSettings()
        listenIsExactTimeFlow()
    }

    fun handleEvent(event: NotificationsEvent) {
        when (event) {
            is NotificationsEvent.EnableSchedule -> {
                if (_uiState.value.isPermissionGranted) {
                    updateScheduleState(event.time, true)
                    startSchedule(event.time, event.days)
                } else {
                    _uiState.update { it.copy(showPermissionDialog = true) }
                }
            }

            is NotificationsEvent.DisableSchedule -> {
                if (_uiState.value.isPermissionGranted) {
                    updateScheduleState(event.time, false)
                    cancelSchedule(event.time, event.days)
                } else {
                    _uiState.update { it.copy(showPermissionDialog = true) }
                }
            }

            is NotificationsEvent.DeleteSchedule -> {
                deleteScheduleInState(event.time)
                deleteTimeSchedule(event.time, event.days)
            }

            is NotificationsEvent.CreateSchedule -> {
                val isActive = _uiState.value.isPermissionGranted
                viewModelScope.launch(Dispatchers.IO) {
                    if (addSchedule(event.time, event.days, isActive) > 0) {
                        addScheduleInState(event.time, event.days)
                    }
                }
            }

            is NotificationsEvent.EditSchedule -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val schedule = event.schedule
                    val oldTime = event.oldSchedule.time
                    if (schedule != null && schedule.time != oldTime) {
                        deleteTimeSchedule(oldTime, schedule.daysOfWeek)
                        val itemId = addSchedule(
                            schedule.time,
                            schedule.daysOfWeek,
                            schedule.isActive.first()
                        )
                        if (itemId > 0) {
                            updateScheduleInState(oldTime, schedule.time, schedule.daysOfWeek)
                        }
                    } else if (schedule != null) {
                        updateSchedule(schedule.time, schedule.daysOfWeek)
                        updateScheduleInState(oldTime, schedule.time, schedule.daysOfWeek)
                    }
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
                this@NotificationsViewModel._uiState.value.schedules.forEach { schedule ->
                    cancelSchedule(schedule.time, schedule.daysOfWeek)
                    startSchedule(schedule.time, schedule.daysOfWeek)
                }
            }
        }
    }

    private fun loadSavedSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val listSchedules = notificationRepository.getSchedules()
            _uiState.update { state ->
                state.copy(schedules = listSchedules)
            }

//            notificationRepository.getSchedules()
//                .distinctUntilChanged()
//                .flowOn(Dispatchers.IO)
//                .collectLatest { listSchedules ->
//                    _uiState.update { state ->
//                        state.copy(schedules = listSchedules)
//                    }
//                }
        }
    }

    private fun disableAllNotifications() {
        viewModelScope.launch {
            _uiState.value.schedules
                .forEach {
                    updateScheduleState(it.time, false)
                }
        }
    }

    private fun deleteTimeSchedule(time: String, days: ImmutableSet<DayOfWeek>) {
        viewModelScope.launch(Dispatchers.IO) {
            cancelSchedule(time, days)
            notificationRepository.deleteSchedule(time)
        }
    }

    private fun updateScheduleState(time: String, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationRepository.updateScheduleState(time, isActive)
        }
    }

    private fun updateSchedule(time: String, days: ImmutableSet<DayOfWeek>) {
        viewModelScope.launch(Dispatchers.IO) {

            Log.i("UPDATE_SCHEDULE", "upd ${notificationRepository.getNotificationSchedule(time)}")
            Log.i("UPDATE_SCHEDULE", "upd ${days}")
            notificationRepository.updateScheduleDays(time, days)
        }
    }

    private fun addScheduleInState(time: String, days: ImmutableSet<DayOfWeek>) {
        val mutableList = _uiState.value.schedules.toMutableList()
        if (mutableList.none { it.time == time }) {
            val isActiveFlow = notificationRepository.getFlowIsActive(time)
            mutableList.add(NotificationScheduleWithFlow(time, days, isActiveFlow))
            _uiState.update {
                it.copy(schedules = mutableList.toImmutableList())
            }
        }
    }

    private fun deleteScheduleInState(time: String) {
        val mutableList = _uiState.value.schedules.filter { it.time != time }
        _uiState.update {
            it.copy(schedules = mutableList.toImmutableList())
        }
    }

    private fun updateScheduleInState(
        oldTime: String,
        time: String,
        days: ImmutableSet<DayOfWeek>
    ) {
        val mutableList = _uiState.value.schedules.filter { it.time != oldTime }.toMutableList()
        if (mutableList.none { it.time == time }) {
            val isActiveFlow = notificationRepository.getFlowIsActive(time)
            mutableList.add(NotificationScheduleWithFlow(time, days, isActiveFlow))
            _uiState.update {
                it.copy(schedules = mutableList.toImmutableList())
            }
        }
    }

    private fun saveChanges() {
        viewModelScope.launch {
            notificationRepository.updateSchedules(_uiState.value.schedules.mapToNotificationScheduleList())
        }
    }

    private suspend fun addSchedule(
        time: String,
        days: ImmutableSet<DayOfWeek>,
        isActive: Boolean
    ): Long {
        startSchedule(time, days)
        val schedule = NotificationSchedule(time, days, isActive)
        return notificationRepository.addSchedule(schedule)
    }

    private fun startSchedule(time: String, daysOfWeek: ImmutableSet<DayOfWeek>) {
        Log.i("NOTIFICATIONS", "scheduleNotifications $time")
        viewModelScope.launch {
            alarmScheduler.createSchedule(time, daysOfWeek)
        }
    }

    private fun cancelSchedule(time: String, daysOfWeek: ImmutableSet<DayOfWeek>) {
        alarmScheduler.cancel(time, daysOfWeek)
    }

    private suspend fun List<NotificationScheduleWithFlow>.mapToNotificationScheduleList(): List<NotificationSchedule> {
        return this.map { it.mapToNotificationSchedule() }
    }

    private suspend fun NotificationScheduleWithFlow.mapToNotificationSchedule(): NotificationSchedule {
        return NotificationSchedule(time, daysOfWeek, isActive.first())
    }

}