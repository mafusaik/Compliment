package com.example.compliment.models

import com.example.compliment.data.model.NotificationSchedule
import java.time.DayOfWeek

sealed class NotificationsEvent {
    data class EnableSchedule(val schedule: NotificationSchedule) : NotificationsEvent()
    data class DisableSchedule(val schedule: NotificationSchedule) : NotificationsEvent()
    data class DeleteSchedule(val schedule: NotificationSchedule) : NotificationsEvent()
    data class CreateSchedule(val time: String, val days: Set<DayOfWeek>) : NotificationsEvent()
    data class EditSchedule(val oldSchedule: NotificationSchedule, val schedule: NotificationSchedule) : NotificationsEvent()
    data object SaveSchedules : NotificationsEvent()
    data class ShowAddScheduleDialog(val isShow: Boolean, val currentSchedule: NotificationSchedule? = null) : NotificationsEvent()
    data class PermissionResult(val isGranted: Boolean) : NotificationsEvent()
    data class ShowPermissionDialog(val isShow: Boolean) : NotificationsEvent()
}