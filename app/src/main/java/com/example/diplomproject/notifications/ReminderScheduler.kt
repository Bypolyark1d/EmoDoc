package com.example.diplomproject.notifications

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.diplomproject.data.Reminder
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.time.Duration


object ReminderScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleReminder(context: Context, reminder: Reminder) {
        val triggerTime = LocalDateTime.of(
            reminder.year,
            reminder.month,
            reminder.day,
            reminder.hour,
            reminder.minute
        )
        val now = LocalDateTime.now()
        val delayMillis = Duration.between(now, triggerTime).toMillis()

        if (delayMillis > 0) {
            val inputData = Data.Builder()
                .putLong("reminder_id", reminder.id)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("reminder_${reminder.id}")
                .build()

            WorkManager.getInstance(context.applicationContext)
                .enqueue(workRequest)
        } else {
            Log.w("ReminderScheduler", "Cannot schedule reminder in the past: $triggerTime")
        }
    }
}