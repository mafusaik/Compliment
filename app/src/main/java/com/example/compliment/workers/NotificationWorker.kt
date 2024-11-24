package com.example.compliment.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.compliment.MainActivity
import com.example.compliment.data.repositories.ComplimentsRepository
import com.example.compliment.utils.Constants
import java.time.DayOfWeek
import java.time.LocalDate

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ComplimentsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notificationId = System.currentTimeMillis().toInt()

        val daysString = inputData.getString("days") ?: return Result.failure()
        val days = daysString.split(",").map { DayOfWeek.valueOf(it) }.toSet()
        Log.i("NOTIFICATIONS", "doWork $days")
        val randomCompliment = repository.getCompliment()

        val currentDay = LocalDate.now().dayOfWeek
        if (currentDay in days) {
            sendNotification(randomCompliment, notificationId)
        }

        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(message: String, notificationId: Int) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        notificationManager.createNotificationChannel(createChannel())

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(Constants.KEY_NOTIFICATION_TEXT, message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = createNotification(message, pendingIntent)

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotification(message: String, pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(applicationContext, Constants.CHANNEL_ID)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createChannel() =
        NotificationChannelCompat.Builder(
            Constants.CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setName("Reminders")
            .build()
}