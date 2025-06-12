package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.foundation.shape.RoundedCornerShape

data class SubjectOverview(
    val name: String,
    val icon: String,
    val currentGrade: Double?,
    val prelim: Double?,
    val midterm: Double?,
    val final: Double?,
    val status: SubjectStatus,
    val message: String
)

enum class SubjectStatus { ALERT, ON_TRACK }

@Composable
fun HomeScreen(
    userName: String = "Student",
    currentPeriod: String = "Midterms",
    week: Int = 8,
    totalWeeks: Int = 18,
    gwa: Double = 2.85,
    subjectsAtRisk: Int = 2,
    passingRate: Int = 71,
    targetProgress: Int = 85,
    prelim: Double = 2.1,
    midterm: Double = 1.5,
    final: Double = 1.8
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Subjects", "Analytics", "Goals")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF43CEA2), Color(0xFF185A9D))
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF43CEA2), Color(0xFF185A9D))
                    )
                )
                .padding(top = 32.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val (title, subtitle) = when (selectedTab) {
                0 -> "Academic Dashboard" to "Current Period: $currentPeriod - Week $week of $totalWeeks"
                1 -> "Grade Input" to "Enter your latest assessment scores"
                else -> "Academic Dashboard" to ""
            }
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, color = if (selectedTab == index) Color(0xFF185A9D) else Color.Gray) }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> OverviewTab(gwa, subjectsAtRisk, passingRate, targetProgress, prelim, midterm, final)
            1 -> SubjectsTab()
            2 -> AnalyticsTab()
            3 -> GoalsTab()
        }
    }
}

@Composable
fun OverviewTab(
    gwa: Double,
    subjectsAtRisk: Int,
    passingRate: Int,
    targetProgress: Int,
    prelim: Double,
    midterm: Double,
    final: Double
) {
    val subjects = listOf(
        SubjectOverview(
            name = "Statistics",
            icon = "\uD83D\uDCCA",
            currentGrade = 1.8,
            prelim = 2.1,
            midterm = 1.5,
            final = null,
            status = SubjectStatus.ALERT,
            message = "You need at least 2.8 in Finals to achieve a passing grade of 2.0."
        ),
        SubjectOverview(
            name = "Programming",
            icon = "\uD83D\uDCBB",
            currentGrade = 1.2,
            prelim = 1.0,
            midterm = 1.4,
            final = null,
            status = SubjectStatus.ON_TRACK,
            message = "Maintain 1.5 or better in Finals to keep your excellent standing."
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(title = "Overall GWA", value = gwa.toString(), modifier = Modifier.weight(1f))
            StatCard(title = "Subjects at Risk", value = subjectsAtRisk.toString(), modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(title = "Passing Rate", value = "$passingRate%", valueColor = Color(0xFF43CEA2), modifier = Modifier.weight(1f), highlight = true)
            StatCard(title = "Target Progress", value = "$targetProgress%", valueColor = Color(0xFF43CEA2), modifier = Modifier.weight(1f), highlight = true)
        }
        Spacer(modifier = Modifier.height(16.dp))
        subjects.forEach { subject ->
            SubjectDashboardCard(subject)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SubjectDashboardCard(subject: SubjectOverview) {
    val borderColor = when (subject.status) {
        SubjectStatus.ALERT -> Color(0xFFD32F2F)
        SubjectStatus.ON_TRACK -> Color(0xFF43CEA2)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(subject.icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(subject.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = subject.currentGrade?.let { String.format("%.1f", it) } ?: "--",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = borderColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GradeColumn("PRELIM", subject.prelim)
                GradeColumn("MIDTERM", subject.midterm)
                GradeColumn("FINAL", subject.final)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (subject.status == SubjectStatus.ALERT) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                    border = BorderStroke(1.dp, Color(0xFFFFECB3))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\u26A0\uFE0F", fontSize = 16.sp, color = Color(0xFFD32F2F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(subject.message, color = Color(0xFF856404), fontSize = 14.sp)
                    }
                }
            } else if (subject.status == SubjectStatus.ON_TRACK) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
                    border = BorderStroke(1.dp, Color(0xFFB2DFDB))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\u2705", fontSize = 16.sp, color = Color(0xFF43CEA2))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(subject.message, color = Color(0xFF2E7D32), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GradeColumn(label: String, value: Double?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(
            text = value?.let { String.format("%.1f", it) } ?: "--",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = if (value == null) Color.Gray else Color.Black
        )
    }
}

@Composable
fun StatCard(title: String, value: String, valueColor: Color = Color.Black, modifier: Modifier = Modifier, highlight: Boolean = false) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (highlight) BorderStroke(2.dp, Color(0xFF43CEA2)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 13.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = valueColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsTab() {
    // State for form fields
    var assessmentType by remember { mutableStateOf("Quiz") }
    val assessmentTypes = listOf("Quiz", "Exam", "Assignment", "Project")
    var scoreObtained by remember { mutableStateOf("") }
    var totalScore by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("03/15/2024") } // Default/mock date
    var expanded by remember { mutableStateOf(false) }

    // State for assessments
    var assessments by remember { mutableStateOf(listOf<AssessmentEntry>()) }

    // Add grade logic
    fun addAssessment() {
        if (scoreObtained.isNotBlank() && totalScore.isNotBlank() && weight.isNotBlank()) {
            assessments = assessments + AssessmentEntry(
                type = assessmentType,
                score = scoreObtained.toInt(),
                total = totalScore.toInt(),
                weight = weight.toInt(),
                date = date
            )
            scoreObtained = ""
            totalScore = ""
            weight = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "\uD83D\uDCCA Statistics - Midterm Period",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF185A9D)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Assessment Type", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = assessmentType,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            assessmentTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        assessmentType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Score Obtained", fontSize = 13.sp)
                            OutlinedTextField(
                                value = scoreObtained,
                                onValueChange = { scoreObtained = it.filter { c -> c.isDigit() } },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Score", fontSize = 13.sp)
                            OutlinedTextField(
                                value = totalScore,
                                onValueChange = { totalScore = it.filter { c -> c.isDigit() } },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Weight (%)", fontSize = 13.sp)
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it.filter { c -> c.isDigit() } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Date", fontSize = 13.sp)
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pick date",
                                tint = Color(0xFF43CEA2)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Gradient Add Grade Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF43CEA2), Color(0xFF185A9D))
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                    ) {
                        TextButton(
                            onClick = { addAssessment() },
                            modifier = Modifier.fillMaxSize(),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Add Grade", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            // Current Grades Card
            if (assessments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color(0xFF43CEA2)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Current Midterm Grades", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        assessments.forEachIndexed { idx, entry ->
                            Text(
                                text = "${entry.type} ${idx + 1}: ${entry.score}/${entry.total} (${entry.weight}%) = ${entry.computedGradeString()}",
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val computed = computePeriodGrade(assessments)
                        Text(
                            text = "Computed Midterm Grade: ${String.format("%.2f", computed)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF185A9D),
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
        // Floating Action Button
        FloatingActionButton(
            onClick = {
                // TODO: Add logic to save assessment and update grades
            },
            containerColor = Color(0xFF43CEA2),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Assessment")
        }
    }
}

data class AssessmentEntry(
    val type: String,
    val score: Int,
    val total: Int,
    val weight: Int,
    val date: String
) {
    fun computedGrade(): Double = (score.toDouble() / total.toDouble()) * weight.toDouble() / 100 * 5 // 5-point scale
    fun computedGradeString(): String = String.format("%.1f", computedGrade())
}

fun computePeriodGrade(assessments: List<AssessmentEntry>): Double {
    if (assessments.isEmpty()) return 0.0
    val totalWeighted = assessments.sumOf { it.computedGrade() }
    return totalWeighted
}

@Composable
fun AnalyticsTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Analytics view coming soon.")
    }
}

@Composable
fun GoalsTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Goals view coming soon.")
    }
} 