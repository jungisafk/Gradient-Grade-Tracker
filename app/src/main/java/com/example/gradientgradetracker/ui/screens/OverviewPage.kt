package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.gradientgradetracker.data.model.AssessmentEntry
import com.example.gradientgradetracker.data.model.SubjectOverview
import com.example.gradientgradetracker.data.model.SubjectStatus
import com.example.gradientgradetracker.data.model.computePeriodGrade

// Remove TrackerSubject data class and replace usages with Subject from HomeScreen or model.
// In SubjectDashboardCard, remove references to subject.icon and subject.message.

@Composable
fun OverviewTab(
    subjects: List<SubjectOverview>
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F9FF)) // subtle blue-tinted background
            .padding(16.dp)
    ) {
        // Compute stats from user data
        val periodNames = listOf("Prelim", "Midterm", "Final")
        val periodGrades = periodNames.map { period: String ->
            subjects.map { subject: SubjectOverview -> computePeriodGrade(subject.assessments[period] ?: emptyList()) }
        }
        val gwa = periodGrades.flatten().filter { grade: Double -> grade > 0.0 }.average().takeIf { avg -> !avg.isNaN() } ?: 0.0
        val subjectFinals = subjects.map { subject: SubjectOverview -> computePeriodGrade(subject.assessments["Final"] ?: emptyList()) }
        val subjectsAtRisk = subjectFinals.count { grade: Double -> grade < 3.0 }
        val passingRate = if (subjectFinals.isNotEmpty()) (subjectFinals.count { grade: Double -> grade >= 3.0 } * 100 / subjectFinals.size) else 0
        val targetProgress = if (subjectFinals.isNotEmpty()) (subjectFinals.count { grade: Double -> grade <= 2.0 } * 100 / subjectFinals.size) else 0
        val prelim = subjects.map { subject: SubjectOverview -> computePeriodGrade(subject.assessments["Prelim"] ?: emptyList()) }.filter { grade: Double -> grade > 0.0 }.average().takeIf { avg -> !avg.isNaN() } ?: 0.0
        val midterm = subjects.map { subject: SubjectOverview -> computePeriodGrade(subject.assessments["Midterm"] ?: emptyList()) }.filter { grade: Double -> grade > 0.0 }.average().takeIf { avg -> !avg.isNaN() } ?: 0.0
        val final = subjectFinals.filter { grade: Double -> grade > 0.0 }.average().takeIf { avg -> !avg.isNaN() } ?: 0.0

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                title = "Overall GWA",
                value = String.format("%.2f", gwa),
                valueColor = Color(0xFF185A9D),
                modifier = Modifier.weight(1f),
                highlight = true
            )
            StatCard(
                title = "Subjects at Risk",
                value = subjectsAtRisk.toString(),
                valueColor = Color(0xFFFFC107),
                modifier = Modifier.weight(1f),
                highlight = true
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard(
                title = "Passing Rate",
                value = "$passingRate%",
                valueColor = Color(0xFF43CEA2),
                modifier = Modifier.weight(1f),
                highlight = true
            )
            StatCard(
                title = "Target Progress",
                value = "$targetProgress%",
                valueColor = Color(0xFFFFC107),
                modifier = Modifier.weight(1f),
                highlight = true
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // Display subject cards if needed
        subjects.forEach { subject: SubjectOverview ->
            // TODO: Convert to SubjectOverview if SubjectDashboardCard requires it, or update SubjectDashboardCard to accept TrackerSubject
            // SubjectDashboardCard(subject)
        }
    }
}

@Composable
fun SubjectDashboardCard(subject: SubjectOverview) {
    val (bgColor, borderColor) = when (subject.status) {
        SubjectStatus.ALERT -> Pair(Color(0xFFFFF8E1), Color(0xFFFFC107)) // yellow bg, yellow border
        SubjectStatus.ON_TRACK -> Pair(Color(0xFFE3F2FD), Color(0xFF185A9D)) // blue bg, blue border
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        Text("This subject is at risk. Monitor grades closely.", color = Color(0xFF856404), fontSize = 14.sp)
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
                        Text("This subject is on track. Keep up the good work!", color = Color(0xFF2E7D32), fontSize = 14.sp)
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