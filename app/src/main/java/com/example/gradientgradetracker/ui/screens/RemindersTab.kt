package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gradientgradetracker.data.model.Reminder
import com.example.gradientgradetracker.data.model.ReminderType
import com.example.gradientgradetracker.data.repository.ReminderRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersTab() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val repo = remember { ReminderRepository() }
    var reminders by remember { mutableStateOf(listOf<Reminder>()) }

    LaunchedEffect(userId) {
        if (userId != null) {
            // initial load
            repo.getReminders(userId).collect { reminders = it }
        }
    }
    DisposableEffect(userId) {
        val reg = if (userId != null) repo.listenReminders(userId) { reminders = it } else null
        onDispose { reg?.remove() }
    }

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var dateMs by remember { mutableStateOf(System.currentTimeMillis()) }
    var type by remember { mutableStateOf(ReminderType.QUIZ) }
    var isAdding by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Reminders", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            Button(onClick = { isAdding = true }, enabled = userId != null) { Text("Add") }
        }
        Spacer(Modifier.height(8.dp))
        if (reminders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reminders yet")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reminders, key = { it.id }) { r ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(r.title, style = MaterialTheme.typography.titleMedium)
                            if (r.description.isNotBlank()) Text(r.description, style = MaterialTheme.typography.bodyMedium)
                            Text("Type: ${r.type}")
                            Text("Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(r.date))}")
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                TextButton(onClick = {
                                    if (userId != null) scope.launch { repo.deleteReminder(userId, r.id) }
                                }) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isAdding && userId != null) {
        AlertDialog(
            onDismissRequest = { isAdding = false },
            title = { Text("New Reminder") },
            text = {
                Column {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                    Spacer(Modifier.height(8.dp))
                    // Simple date input in millis for now
                    OutlinedTextField(
                        value = dateMs.toString(),
                        onValueChange = { v -> dateMs = v.filter { it.isDigit() }.toLongOrNull() ?: dateMs },
                        label = { Text("Date (millis)") }
                    )
                    Spacer(Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = type.name,
                            onValueChange = {}, readOnly = true,
                            label = { Text("Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            ReminderType.values().forEach { t ->
                                DropdownMenuItem(text = { Text(t.name) }, onClick = { type = t; expanded = false })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val reminder = Reminder(
                        title = title.trim(),
                        description = desc.trim(),
                        date = dateMs,
                        type = type,
                        userId = userId
                    )
                    scope.launch {
                        repo.addReminder(userId, reminder)
                    }
                    isAdding = false
                    title = ""; desc = ""
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { isAdding = false }) { Text("Cancel") } }
        )
    }
}
