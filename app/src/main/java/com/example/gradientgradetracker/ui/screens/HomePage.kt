package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradientgradetracker.data.model.Reminder
import com.example.gradientgradetracker.data.model.Subject
import com.example.gradientgradetracker.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomePage(
    userId: String,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val subjects by viewModel.subjects.collectAsState()
    val reminders by viewModel.reminders.collectAsState()
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }

    LaunchedEffect(userId) {
        viewModel.loadData(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grade Tracker") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showAddReminderDialog = true },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Add Reminder")
                }
                FloatingActionButton(
                    onClick = { showAddSubjectDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Subject")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Your Subjects",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    onDelete = { viewModel.deleteSubject(subject.id) },
                    onUpdateGrade = { type, grade ->
                        viewModel.updateGrade(subject.id, type, grade)
                    }
                )
            }

            item {
                Text(
                    text = "Upcoming Reminders",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(reminders) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onDelete = { viewModel.deleteReminder(userId, reminder.id) }
                )
            }
        }
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, code, targetGrade ->
                viewModel.addSubject(userId, name, code, targetGrade)
                showAddSubjectDialog = false
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            subjects = subjects,
            onDismiss = { showAddReminderDialog = false },
            onConfirm = { subjectId, title, description, date, type ->
                viewModel.addReminder(userId, subjectId, title, description, date, type)
                showAddReminderDialog = false
            }
        )
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onDelete: () -> Unit,
    onUpdateGrade: (String, Double) -> Unit
) {
    var showGradeDialog by remember { mutableStateOf(false) }
    var selectedGradeType by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "Code: ${subject.code}",
                        style = MaterialTheme.typography.body2
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Subject")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Target Grade: ${subject.targetGrade}",
                style = MaterialTheme.typography.body1
            )

            Button(
                onClick = { showGradeDialog = true },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Update Grade")
            }
        }
    }

    if (showGradeDialog) {
        AlertDialog(
            onDismissRequest = { showGradeDialog = false },
            title = { Text("Update Grade") },
            text = {
                Column {
                    TextField(
                        value = selectedGradeType,
                        onValueChange = { selectedGradeType = it },
                        label = { Text("Grade Type (e.g., Midterm, Final)") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = "",
                        onValueChange = { },
                        label = { Text("Grade") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Add grade update logic
                        showGradeDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGradeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = reminder.description,
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Date: ${SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(reminder.date))}",
                    style = MaterialTheme.typography.body2
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Reminder")
            }
        }
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var targetGrade by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Subject") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Subject Code") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = targetGrade,
                    onValueChange = { targetGrade = it },
                    label = { Text("Target Grade") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val grade = targetGrade.toDoubleOrNull() ?: 0.0
                    onConfirm(name, code, grade)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddReminderDialog(
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Long, String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Reminder") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (MM/dd/yyyy)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type (ASSIGNMENT, EXAM, OTHER)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val dateMillis = try {
                        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                            .parse(date)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    }
                    onConfirm(selectedSubject, title, description, dateMillis, type)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 