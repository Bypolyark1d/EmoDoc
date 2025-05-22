package com.example.diplomproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.diplomproject.ViewModel.RemindersViewModel
import com.example.diplomproject.data.Reminder
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RemindersScreen(onMenuClick: () -> Unit, viewModel: RemindersViewModel) {
    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    LaunchedEffect(reminders) {
        val now = Calendar.getInstance()
        reminders.forEach { reminder ->
            val reminderTime = Calendar.getInstance().apply {
                set(reminder.year, reminder.month - 1, reminder.day, reminder.hour, reminder.minute, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (reminderTime.before(now)) {
                viewModel.deleteReminder(reminder.id)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffece0))
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                TopBar(title = "Напоминания", onMenuClick = onMenuClick)
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 16.dp)
                        .shadow(10.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E756E).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.card_profile),
                        contentDescription = "Эмоция",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFffece0), RoundedCornerShape(12.dp))
                        .padding(vertical = 14.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Добавить напоминание",
                        color = Color(0xFF2A3439),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(
                        onClick = { showAddDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            tint = Color(0xFF2A3439),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (reminders.isEmpty()) {
                item {
                    Text(
                        text = "Напоминания отсутствуют",
                        color = Color(0xFF2A3439),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(reminders) { reminder ->
                ReminderItem(reminder = reminder, onDelete = { viewModel.deleteReminder(it) })
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialogCustom(
            onDismiss = { showAddDialog = false },
            onAdd = { reminder ->
                viewModel.addReminder(reminder)
                showAddDialog = false
            }
        )
    }

}

@Composable
fun ReminderItem(reminder: Reminder, onDelete: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFffece0))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "%02d.%02d.%d в %02d:%02d".format(
                    reminder.day,
                    reminder.month,
                    reminder.year,
                    reminder.hour,
                    reminder.minute
                ),
                color = Color(0xFF2A3439),
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp
            )
            IconButton(
                onClick = { onDelete(reminder.id) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color(0xFFed9a66),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AddReminderDialogCustom(onDismiss: () -> Unit, onAdd: (Reminder) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedYear = year
                selectedMonth = month
                selectedDay = day
            },
            selectedYear,
            selectedMonth,
            selectedDay
        ).apply {
            datePicker.minDate = System.currentTimeMillis() // запрещает выбор дат до текущего дня
        }
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
            },
            selectedHour,
            selectedMinute,
            true
        )
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .background(Color(0xFFffece0), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
                .fillMaxWidth(0.9f)
        ) {
            Column {
                Text(
                    "Добавить напоминание",
                    color = Color(0xFF4E756E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Дата: %02d.%02d.%d".format(selectedDay, selectedMonth + 1, selectedYear),
                        modifier = Modifier.clickable { datePickerDialog.show() },
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2A3439)
                    )
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Выбрать дату",
                            tint = Color(0xFFed9a66)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Время: %02d:%02d".format(selectedHour, selectedMinute),
                        modifier = Modifier.clickable { timePickerDialog.show() },
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2A3439)
                    )
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Выбрать время",
                            tint = Color(0xFFed9a66)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                            set(Calendar.DAY_OF_MONTH, selectedDay)
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }

                        val currentTime = Calendar.getInstance()

                        if (selectedCalendar.before(currentTime)) {
                            Toast.makeText(context, "Нельзя установить напоминание в прошлом", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        onAdd(
                            Reminder(
                                id = System.currentTimeMillis(),
                                year = selectedYear,
                                month = selectedMonth + 1,
                                day = selectedDay,
                                hour = selectedHour,
                                minute = selectedMinute
                            )
                        )
                    }) {
                        Text("Добавить", color = Color(0xFF4E756E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

