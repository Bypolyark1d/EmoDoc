package com.example.diplomproject.notifications

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.diplomproject.data.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.collections.toMutableList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val Context.dataStore by preferencesDataStore("reminders")

object ReminderRepository {
    private val REMINDERS_KEY = stringPreferencesKey("reminders")

    suspend fun saveReminder(context: Context, reminder: Reminder) {
        val currentList = getReminders(context).toMutableList()
        currentList.add(reminder)
        val json = Json.encodeToString(currentList)
        context.dataStore.edit { it[REMINDERS_KEY] = json }
    }

    suspend fun getReminders(context: Context): List<Reminder> {
        val prefs = context.dataStore.data.first()
        val json = prefs[REMINDERS_KEY] ?: return emptyList()
        return Json.decodeFromString(json)
    }

    suspend fun deleteReminder(context: Context, reminderId: Long) {
        val currentList = getReminders(context).toMutableList()
        val filtered = currentList.filter { it.id != reminderId }
        val json = Json.encodeToString(filtered)
        context.dataStore.edit { it[REMINDERS_KEY] = json }
    }
}