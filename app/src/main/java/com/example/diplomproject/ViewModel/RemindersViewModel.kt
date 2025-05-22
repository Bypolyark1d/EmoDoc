package com.example.diplomproject.ViewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.diplomproject.data.Reminder
import com.example.diplomproject.notifications.ReminderRepository
import com.example.diplomproject.notifications.ReminderScheduler
import com.example.diplomproject.notifications.ReminderWorker
import com.example.diplomproject.notifications.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


class RemindersViewModel(private val context: Context) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            val list = ReminderRepository.getReminders(context)
            _reminders.value = list
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            ReminderRepository.saveReminder(context, reminder)
            ReminderScheduler.scheduleReminder(context, reminder)
            loadReminders()
        }
    }

    fun deleteReminder(reminderId: Long) {
        viewModelScope.launch {
            ReminderRepository.deleteReminder(context, reminderId)
            loadReminders()
        }
    }
}