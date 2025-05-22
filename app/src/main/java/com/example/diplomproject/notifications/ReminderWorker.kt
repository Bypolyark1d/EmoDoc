package com.example.diplomproject.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.diplomproject.R
import kotlinx.coroutines.runBlocking

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val reminderId = inputData.getLong("reminder_id", -1L)
        if (reminderId == -1L) {
            Log.e("ReminderWorker", "Reminder ID missing")
            return Result.failure()
        }

        createNotificationChannel()
        showNotification()
        runBlocking {
            ReminderRepository.deleteReminder(applicationContext, reminderId)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "breathing_reminders",
                "Напоминания",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для напоминаний"
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notificationId = System.currentTimeMillis().toInt()
        val notification = NotificationCompat.Builder(applicationContext, "breathing_reminders")
            .setContentTitle("Напоминание")
            .setContentText("Пора выполнить дыхательную практику!")
            .setSmallIcon(R.drawable.logoicon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }
}