package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gradientgradetracker.data.model.AssessmentEntry
import com.example.gradientgradetracker.data.model.Subject
import com.example.gradientgradetracker.data.model.computePeriodGrade
import java.util.UUID

// Moved SubjectUI out of HomeScreen to make it accessible
data class SubjectUI(
    val subject: Subject,
    val icon: String = "\uD83D\uDCD6",
    val assessments: MutableMap<String, MutableList<AssessmentEntry>> = mutableMapOf(
        "Prelim" to mutableListOf(),
        "Midterm" to mutableListOf(),
        "Final" to mutableListOf()
    )
)

class AssessmentInputState(
    type: String = "Quiz",
    scoreObtained: String = "",
    totalScore: String = "",
    weight: String = "",
    date: String = "03/15/2024",
    expanded: Boolean = false
) {
    var type by mutableStateOf(type)
    var scoreObtained by mutableStateOf(scoreObtained)
    var totalScore by mutableStateOf(totalScore)
    var weight by mutableStateOf(weight)
    var date by mutableStateOf(date)
    var expanded by mutableStateOf(expanded)
    var errorMessage by mutableStateOf<String?>(null)
}

@Composable
private fun GradeChip(label: String, value: Double) {
    val color = when {
        value < 3.0 -> Color(0xFFD32F2F)
        value < 3.5 -> Color(0xFFFFA000)
        else -> Color(0xFF2E7D32)
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$label:", color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(String.format("%.2f", value), color = color, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsTab(
    subjects: List<SubjectUI>,
    onAddSubject: (Subject, String) -> Unit,
    onRemoveSubject: (String) -> Unit,
    onAddAssessment: (String, String, AssessmentEntry) -> Unit,
    onRemoveAssessment: (String, String, Int) -> Unit
) {
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newSubjectIcon by remember { mutableStateOf("\uD83D\uDCD6") }
    var showInitialAssessments by remember { mutableStateOf(false) }

    val assessmentTypes = listOf("Quiz", "Activities", "Exam")
    val inputStates = remember { mutableStateMapOf<Pair<String, String>, AssessmentInputState>() }

    val initialPeriods = listOf("Prelim", "Midterm", "Final")
    val initialCategories = listOf("Quiz", "Activities", "Exam")
    val initialInputs = remember { mutableStateMapOf<Pair<String, String>, AssessmentInputState>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 80.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showAddSubjectDialog = true },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Add Subject")
            }

            if (showAddSubjectDialog) {
                AlertDialog(
                    onDismissRequest = { showAddSubjectDialog = false },
                    title = { Text("Add New Subject") },
                    text = {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            OutlinedTextField(
                                value = newSubjectName,
                                onValueChange = { newSubjectName = it },
                                label = { Text("Subject Name") }
                            )
                            OutlinedTextField(
                                value = newSubjectIcon,
                                onValueChange = { newSubjectIcon = it },
                                label = { Text("Icon (emoji)") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showInitialAssessments = !showInitialAssessments }
                            ) {
                                Text(
                                    text = "Initial Assessments (optional)",
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF185A9D)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (showInitialAssessments) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                            if (showInitialAssessments) {
                                Spacer(modifier = Modifier.height(4.dp))
                                initialPeriods.forEach { period ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFF43CEA2))
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(period, fontWeight = FontWeight.Bold, color = Color(0xFF185A9D))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            initialCategories.forEach { category ->
                                                val key = period to category
                                                val st = initialInputs.getOrPut(key) { AssessmentInputState(type = category) }
                                                Column(modifier = Modifier.fillMaxWidth()) {
                                                    Text(category, fontWeight = FontWeight.Medium)
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        OutlinedTextField(
                                                            value = st.scoreObtained,
                                                            onValueChange = { st.scoreObtained = it.filter { c -> c.isDigit() || c == '.' } },
                                                            label = { Text("Score") },
                                                            singleLine = true,
                                                            modifier = Modifier.width(80.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        OutlinedTextField(
                                                            value = st.totalScore,
                                                            onValueChange = { st.totalScore = it.filter { c -> c.isDigit() || c == '.' } },
                                                            label = { Text("Total") },
                                                            singleLine = true,
                                                            modifier = Modifier.width(80.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        OutlinedTextField(
                                                            value = st.weight,
                                                            onValueChange = { st.weight = it.filter { c -> c.isDigit() || c == '.' } },
                                                            label = { Text("Weight (%)") },
                                                            singleLine = true,
                                                            modifier = Modifier.width(90.dp)
                                                        )
                                                    }
                                                    if (st.errorMessage != null) {
                                                        Text(st.errorMessage ?: "", color = Color.Red, fontSize = 12.sp)
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                            }
                                            val totalWeightForPeriod = initialCategories.sumOf { (initialInputs[period to it]?.weight?.toDoubleOrNull()) ?: 0.0 }
                                            val totalColor = when {
                                                totalWeightForPeriod > 100.0 -> Color.Red
                                                totalWeightForPeriod == 100.0 -> Color(0xFF2E7D32)
                                                else -> Color(0xFF6D6D6D)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Total weight: ${String.format("%.1f", totalWeightForPeriod)}%",
                                                    color = totalColor,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                if (totalWeightForPeriod != 100.0) {
                                                    Text(
                                                        text = if (totalWeightForPeriod > 100.0) "Exceeds 100%" else "Should sum to 100%",
                                                        color = totalColor,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        val anyOver = initialPeriods.any { p ->
                            initialCategories.sumOf { (initialInputs[p to it]?.weight?.toDoubleOrNull()) ?: 0.0 } > 100.0
                        }
                        TextButton(
                            enabled = !anyOver,
                            onClick = {
                                if (newSubjectName.isNotBlank()) {
                                    val subject = Subject(id = UUID.randomUUID().toString(), name = newSubjectName)
                                    onAddSubject(subject, newSubjectIcon)
                                    initialPeriods.forEach { period ->
                                        initialCategories.forEach { category ->
                                            val key = period to category
                                            val st = initialInputs[key]
                                            if (st != null && st.scoreObtained.isNotBlank() && st.totalScore.isNotBlank()) {
                                                val score = st.scoreObtained.toDoubleOrNull() ?: -1.0
                                                val total = st.totalScore.toDoubleOrNull() ?: -1.0
                                                if (score < 0 || total <= 0 || score > total) {
                                                    st.errorMessage = "Invalid score/total"
                                                } else {
                                                    st.errorMessage = null
                                                }
                                                if (st.errorMessage != null) return@forEach
                                                onAddAssessment(
                                                    subject.id,
                                                    period,
                                                    AssessmentEntry(
                                                        name = category,
                                                        scoreObtained = score,
                                                        totalScore = total,
                                                        weight = st.weight.toDoubleOrNull() ?: 1.0,
                                                        date = st.date,
                                                        assessmentType = category
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    newSubjectName = ""
                                    newSubjectIcon = "\uD83D\uDCD6"
                                    showInitialAssessments = false
                                    initialInputs.clear()
                                    showAddSubjectDialog = false
                                }
                            }
                        ) { Text("Add") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
                    }
                )
            }

            val expandedSubjects = remember { mutableStateMapOf<String, Boolean>() }
            val selectedPeriodBySubject = remember { mutableStateMapOf<String, String>() }
            val perCategoryInputs = remember { mutableStateMapOf<Triple<String, String, String>, AssessmentInputState>() }
            val includeAttendanceBySubject = remember { mutableStateMapOf<String, Boolean>() }
            val attendanceWeightBySubject = remember { mutableStateMapOf<String, String>() }

            subjects.forEach { subjectUI ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, Color(0xFF185A9D))
                ) {
                    val isExpanded = expandedSubjects[subjectUI.subject.id] ?: false
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(subjectUI.icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(subjectUI.subject.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val prelimVal = computePeriodGrade(subjectUI.assessments["Prelim"] ?: emptyList())
                                val midtermVal = computePeriodGrade(subjectUI.assessments["Midterm"] ?: emptyList())
                                val finalVal = computePeriodGrade(subjectUI.assessments["Final"] ?: emptyList())
                                val overall = listOf(prelimVal, midtermVal, finalVal)
                                    .filter { !it.isNaN() }
                                    .let { if (it.isEmpty()) 0.0 else it.average() }
                                GradeChip("Avg", overall)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { onRemoveSubject(subjectUI.subject.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Subject", tint = Color.Red)
                            }
                            IconButton(onClick = {
                                expandedSubjects[subjectUI.subject.id] = !isExpanded
                            }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isExpanded) {
                            val subjectId = subjectUI.subject.id
                            val selectedPeriod = selectedPeriodBySubject.getOrPut(subjectId) { "Prelim" }
                            val periodAssessments = subjectUI.assessments[selectedPeriod] ?: emptyList<AssessmentEntry>()
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF43CEA2))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Period", fontWeight = FontWeight.Bold, color = Color(0xFF185A9D))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        var periodExpanded by remember { mutableStateOf(false) }
                                        ExposedDropdownMenuBox(
                                            expanded = periodExpanded,
                                            onExpandedChange = { periodExpanded = !periodExpanded }
                                        ) {
                                            OutlinedTextField(
                                                value = selectedPeriod,
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(periodExpanded) },
                                                modifier = Modifier.menuAnchor().width(120.dp)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = periodExpanded,
                                                onDismissRequest = { periodExpanded = false }
                                            ) {
                                                listOf("Prelim", "Midterm", "Final").forEach { p ->
                                                    DropdownMenuItem(
                                                        text = { Text(p) },
                                                        onClick = {
                                                            selectedPeriodBySubject[subjectId] = p
                                                            periodExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text("Grade: ${String.format("%.2f", computePeriodGrade(periodAssessments))}", fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (periodAssessments.isNotEmpty()) {
                                        periodAssessments.forEachIndexed { idx, entry ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                val weightLabel = if (entry.weight == 1.0) "" else " (${entry.weight})"
                                                Text("${entry.name}: ${entry.scoreObtained}/${entry.totalScore}$weightLabel", fontSize = 14.sp)
                                                Spacer(modifier = Modifier.weight(1f))
                                                IconButton(onClick = { onRemoveAssessment(subjectUI.subject.id, selectedPeriod, idx) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    // Optional attendance
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val includeAttendance = includeAttendanceBySubject.getOrPut(subjectId) { false }
                                        val attWeight = attendanceWeightBySubject.getOrPut(subjectId) { "5" }
                                        Checkbox(checked = includeAttendance, onCheckedChange = { includeAttendanceBySubject[subjectId] = it })
                                        Text("Include Attendance")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedTextField(
                                            value = attWeight,
                                            onValueChange = { attendanceWeightBySubject[subjectId] = it.filter { c -> c.isDigit() || c == '.' } },
                                            label = { Text("Attendance Weight (%)") },
                                            singleLine = true,
                                            enabled = includeAttendance,
                                            modifier = Modifier.width(180.dp)
                                        )
                                    }
                                    listOf("Quiz", "Activities", "Exam").forEach { category ->
                                        val key = Triple(subjectId, selectedPeriod, category)
                                        val st = perCategoryInputs.getOrPut(key) { AssessmentInputState(type = category) }
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Text(category, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                                OutlinedTextField(
                                                    value = st.scoreObtained,
                                                    onValueChange = { st.scoreObtained = it.filter { c -> c.isDigit() || c == '.' } },
                                                    label = { Text("Score") },
                                                    singleLine = true,
                                                    modifier = Modifier.width(80.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                OutlinedTextField(
                                                    value = st.totalScore,
                                                    onValueChange = { st.totalScore = it.filter { c -> c.isDigit() || c == '.' } },
                                                    label = { Text("Total") },
                                                    singleLine = true,
                                                    modifier = Modifier.width(80.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                OutlinedTextField(
                                                    value = st.weight,
                                                    onValueChange = { st.weight = it.filter { c -> c.isDigit() || c == '.' } },
                                                    label = { Text("Weight (%)") },
                                                    singleLine = true,
                                                    modifier = Modifier.width(90.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                OutlinedTextField(
                                                    value = st.date,
                                                    onValueChange = { st.date = it },
                                                    label = { Text("Date") },
                                                    singleLine = true,
                                                    modifier = Modifier.width(100.dp)
                                                )
                                            }
                                            if (st.errorMessage != null) {
                                                Text(st.errorMessage ?: "", color = Color.Red, fontSize = 12.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    val baseTotal = listOf("Quiz", "Activities", "Exam").sumOf {
                                        (perCategoryInputs[Triple(subjectId, selectedPeriod, it)]?.weight?.toDoubleOrNull()) ?: 0.0
                                    }
                                    val attendanceWeight = (attendanceWeightBySubject[subjectId]?.toDoubleOrNull()) ?: 0.0
                                    val includeAttendance = includeAttendanceBySubject[subjectId] ?: false
                                    val totalWeight = baseTotal + if (includeAttendance) attendanceWeight else 0.0
                                    val totalColor = when {
                                        totalWeight > 100.0 -> Color.Red
                                        totalWeight == 100.0 -> Color(0xFF2E7D32)
                                        else -> Color(0xFF6D6D6D)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Total weight: ${String.format("%.1f", totalWeight)}%",
                                            color = totalColor,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        if (totalWeight != 100.0) {
                                            Text(
                                                text = if (totalWeight > 100.0) "Exceeds 100%" else "Should sum to 100%",
                                                color = totalColor,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            listOf("Quiz", "Activities", "Exam").forEach { category ->
                                                val key = Triple(subjectId, selectedPeriod, category)
                                                val st = perCategoryInputs[key]
                                                if (st != null && st.scoreObtained.isNotBlank() && st.totalScore.isNotBlank() && st.weight.isNotBlank()) {
                                                    val score = st.scoreObtained.toDoubleOrNull() ?: -1.0
                                                    val total = st.totalScore.toDoubleOrNull() ?: -1.0
                                                    if (score < 0 || total <= 0 || score > total) {
                                                        st.errorMessage = "Invalid score/total"
                                                    } else {
                                                        st.errorMessage = null
                                                    }
                                                    if (st.errorMessage != null) return@forEach
                                                    onAddAssessment(
                                                        subjectId,
                                                        selectedPeriod,
                                                        AssessmentEntry(
                                                            name = category,
                                                            scoreObtained = score,
                                                            totalScore = total,
                                                            weight = st.weight.toDoubleOrNull() ?: 1.0,
                                                            date = st.date,
                                                            assessmentType = category
                                                        )
                                                    )
                                                    perCategoryInputs[key] = AssessmentInputState(type = category)
                                                }
                                            }
                                            // Attendance as a synthetic assessment
                                            val includeAtt = includeAttendanceBySubject[subjectId] ?: false
                                            val attW = (attendanceWeightBySubject[subjectId]?.toDoubleOrNull()) ?: 0.0
                                            if (includeAtt && attW > 0) {
                                                onAddAssessment(
                                                    subjectId,
                                                    selectedPeriod,
                                                    AssessmentEntry(
                                                        name = "Attendance",
                                                        scoreObtained = 100.0,
                                                        totalScore = 100.0,
                                                        weight = attW,
                                                        date = "",
                                                        assessmentType = "Attendance"
                                                    )
                                                )
                                            }
                                        },
                                        enabled = totalWeight == 100.0,
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Text("Add All")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
