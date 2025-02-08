package com.glazer.compliment.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableSet
import java.time.DayOfWeek

@Immutable
sealed class NotificationsEvent {
    @Immutable
    data class EnableSchedule(val time: String, val days: ImmutableSet<DayOfWeek>) : NotificationsEvent()
    @Immutable
    data class DisableSchedule(val time: String, val days: ImmutableSet<DayOfWeek>) : NotificationsEvent()
    @Immutable
    data class DeleteSchedule(val time: String, val days: ImmutableSet<DayOfWeek>) : NotificationsEvent()
    @Immutable
    data class CreateSchedule(val time: String, val days: ImmutableSet<DayOfWeek>) : NotificationsEvent()
    @Immutable
    data class EditSchedule(val oldSchedule: NotificationScheduleWithFlow, val schedule: NotificationScheduleWithFlow) : NotificationsEvent()
    data object SaveSchedules : NotificationsEvent()
    @Immutable
    data class ShowAddScheduleDialog(val isShow: Boolean, val currentSchedule: NotificationScheduleWithFlow? = null) : NotificationsEvent()
    @Immutable
    data class PermissionResult(val isGranted: Boolean) : NotificationsEvent()
    @Immutable
    data class ShowPermissionDialog(val isShow: Boolean) : NotificationsEvent()
}