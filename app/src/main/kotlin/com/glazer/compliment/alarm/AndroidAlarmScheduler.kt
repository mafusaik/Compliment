package com.glazer.compliment.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.glazer.compliment.data.sharedprefs.PrefsManager
import com.glazer.compliment.receivers.NotificationReceiver
import com.glazer.compliment.utils.Constants
import kotlinx.collections.immutable.ImmutableSet
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val prefsManager = PrefsManager(context)


    @SuppressLint("ScheduleExactAlarm")
    override fun createSchedule(time: String, daysOfWeek: ImmutableSet<DayOfWeek>) {
        val intent = getIntent(time, daysOfWeek)
        val pendingIntent = getPendingIntent(intent, time)
        val firstTriggerTime = calculateDelay(time, true)

        if (prefsManager.isExactTime) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + firstTriggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + firstTriggerTime,
                pendingIntent
            )
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    override fun createRepeatSchedule(time: String, daysOfWeek: ImmutableSet<DayOfWeek>) {
        val intent = getIntent(time, daysOfWeek)
        val pendingIntent = getPendingIntent(intent, time)
        val newDelay = calculateDelay(time, false)

        if (prefsManager.isExactTime) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                newDelay,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                newDelay,
                pendingIntent
            )
        }
    }

    override fun cancel(time: String, daysOfWeek: ImmutableSet<DayOfWeek>) {
        val intent = getIntent(time, daysOfWeek)
        val pendingIntent = getPendingIntent(intent, time)

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    private fun calculateDelay(time:String, isInitial: Boolean): Long {
        val (hour, minute) = time.split(":").map { it.toInt() }
        val now = LocalDateTime.now()
        val todayAlarm = now
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)

        val nextAlarm = if (todayAlarm.isAfter(now) && isInitial) {
            todayAlarm
        } else {
            todayAlarm.plusDays(1)
        }

        val delay = if (isInitial) System.currentTimeMillis() else 0

        return nextAlarm
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - delay
    }

    private fun getIntent(time: String, daysOfWeek: ImmutableSet<DayOfWeek>): Intent{
        return Intent(context, NotificationReceiver::class.java).apply {
            action = Constants.KEY_NOTIFICATION_FILTER
            putExtra(Constants.KEY_TIME, time)
            putExtra(Constants.KEY_DAYS, daysOfWeek.joinToString(","))
        }
    }

    private fun getPendingIntent(
        intent: Intent,
        time: String,
    ): PendingIntent {
        val uniqueId = time.replace(":", "").toInt()
        return PendingIntent.getBroadcast(
            context,
            uniqueId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}