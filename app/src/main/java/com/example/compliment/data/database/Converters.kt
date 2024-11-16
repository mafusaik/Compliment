package com.example.compliment.data.database

import androidx.room.TypeConverter
import java.time.DayOfWeek

class Converters {

    @TypeConverter
    fun fromDayOfWeekSet(value: Set<DayOfWeek>?): String {
        return value?.joinToString(",") { it.name } ?: ""
    }

    @TypeConverter
    fun toDayOfWeekSet(value: String): Set<DayOfWeek> {
        return value.split(",").mapNotNull {
            try {
                DayOfWeek.valueOf(it.trim())
            } catch (e: IllegalArgumentException) {
                null
            }
        }.toSet()
    }
}