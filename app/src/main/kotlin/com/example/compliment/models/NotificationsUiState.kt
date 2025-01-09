package com.example.compliment.models

import com.example.compliment.data.model.NotificationSchedule
import java.time.DayOfWeek

data class NotificationsUiState(
    val schedules: Set<NotificationSchedule> = emptySet(),
    val selectedDays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val showScheduleDialog: Boolean = false,
    val currentScheduleData: NotificationSchedule? = null,
    val isPermissionGranted: Boolean = true,
    val showPermissionDialog: Boolean = false,
)