package com.glazer.compliment.receivers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.glazer.compliment.MainActivity
import com.glazer.compliment.R
import com.glazer.compliment.alarm.AlarmScheduler
import com.glazer.compliment.data.repositories.ComplimentsRepository
import com.glazer.compliment.data.repositories.NotificationRepository
import com.glazer.compliment.utils.Constants
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.DayOfWeek
import java.time.LocalDate

class NotificationReceiver : BroadcastReceiver() {

    private val alarmScheduler: AlarmScheduler by inject(AlarmScheduler::class.java)
    private val complimentRepository: ComplimentsRepository by inject(ComplimentsRepository::class.java)
    private val notificationRepository: NotificationRepository by inject(NotificationRepository::class.java)

    override fun onReceive(context: Context, intent: Intent) {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.Main + job)
        val action = intent.action

        when (action) {
            Constants.KEY_NOTIFICATION_FILTER -> {
                Log.i("NotificationReceiver", "action NOTIFICATION")
                val time = intent.getStringExtra(Constants.KEY_TIME)
                val daysString = intent.getStringExtra(Constants.KEY_DAYS) ?: ""
                val days = daysString.split(",").map { DayOfWeek.valueOf(it) }.toSet()

                scope.launch {
                    val message = complimentRepository.nextCompliment()

                    val currentDay = LocalDate.now().dayOfWeek
                    if (currentDay in days) {
                        Log.d("NotificationReceiver", "sendNotification $time")
                        sendNotification(context, message)
                    }
                    time?.let {
                        alarmScheduler.createRepeatSchedule(time, days.toImmutableSet())
                    }
                    job.cancel()
                }
            }
            Constants.KEY_BOOT_FILTER -> {
                Log.i("NotificationReceiver", "action BOOT")
                scope.launch(Dispatchers.IO) {
                    notificationRepository.getSchedules()
                        .onEach {schedule->
                            alarmScheduler.createSchedule(schedule.time, schedule.daysOfWeek)
                        }
                }
                    job.cancel()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(context: Context, message: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.createNotificationChannel(createChannel())

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(Constants.KEY_NOTIFICATION_TEXT, message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            message.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo_notify)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }

    private fun createChannel() =
        NotificationChannelCompat.Builder(
            Constants.CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setName("Reminders")
            .build()
}