package com.example.compliment.receivers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.compliment.MainActivity
import com.example.compliment.utils.Constants
import java.time.DayOfWeek
import java.time.LocalDate

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("NotificationReceiver", "Alarm triggered! $action")
        if ("com.example.compliment.NOTIFY" == action) {
            val time = intent.getStringExtra("schedule_time")
            val daysString = intent.getStringExtra("days") ?: ""
            val days = daysString.split(",").map { DayOfWeek.valueOf(it) }.toSet()
            val message = intent.getStringExtra("message") ?: ""


            val currentDay = LocalDate.now().dayOfWeek
            if (currentDay in days) {
                Log.d("NotificationReceiver", "sendNotification $time")
                sendNotification(context, message)
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
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