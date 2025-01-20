package com.example.compliment.data.database

import androidx.room.TypeConverter
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import java.time.DayOfWeek

class Converters {

    @TypeConverter
    fun fromDayOfWeekSet(days: ImmutableSet<DayOfWeek>): String {
        return days.sortedBy { dayOrder.indexOf(it) }
            .joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDayOfWeekSet(value: String): ImmutableSet<DayOfWeek> {
        return value.split(",")
            .map { DayOfWeek.valueOf(it) }
            .sortedBy { dayOrder.indexOf(it) }
            .toImmutableSet()
    }

   // companion object {
        private val dayOrder = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
   // }
}