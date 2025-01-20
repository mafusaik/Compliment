package com.example.compliment.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableSet
import java.time.DayOfWeek

@Immutable
data class NotificationsUiState(
    val schedules: ImmutableList<NotificationScheduleWithFlow> = persistentListOf(),
    val selectedDays: ImmutableSet<DayOfWeek> = DayOfWeek.entries.toImmutableSet(),
    val showScheduleDialog: Boolean = false,
    val currentScheduleData: NotificationScheduleWithFlow? = null,
    val isPermissionGranted: Boolean = true,
    val showPermissionDialog: Boolean = false,
)