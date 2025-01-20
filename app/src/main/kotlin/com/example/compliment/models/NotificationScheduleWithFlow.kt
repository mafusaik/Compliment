package com.example.compliment.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

@Immutable
data class NotificationScheduleWithFlow(
    val time: String,
    val daysOfWeek: ImmutableSet<DayOfWeek>,
    val isActive: Flow<Boolean>
)