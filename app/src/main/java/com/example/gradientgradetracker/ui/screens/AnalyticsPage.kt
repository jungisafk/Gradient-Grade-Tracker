package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.gradientgradetracker.data.model.AssessmentEntry
import com.example.gradientgradetracker.data.model.SubjectOverview
import com.example.gradientgradetracker.data.model.SubjectStatus
import com.example.gradientgradetracker.data.model.computePeriodGrade

@Composable
fun AnalyticsTab(
    subjects: List<SubjectOverview>,
    assessmentsByPeriod: Map<String, List<AssessmentEntry>>
) {
    val periodNames = listOf("Prelim", "Midterm", "Final")
    val periodGrades = periodNames.map { period ->
        computePeriodGrade(assessmentsByPeriod[period] ?: emptyList())
    }
    val subjectPerformances = subjects.map { it.name to (it.currentGrade ?: 0.0) }
    val atRiskSubjects = subjects.filter { it.status == SubjectStatus.ALERT }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
            .padding(bottom = 16.dp)
    ) {
        // Grade Trend Analysis Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Grade Trend Analysis", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                // Grade Trend Chart
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp)) {
                        val width = size.width
                        val height = size.height
                        periodNames.zipWithNext().forEachIndexed { idx, (prev, curr) ->
                            val prevGrade = periodGrades.getOrNull(idx) ?: 0.0
                            val currGrade = periodGrades.getOrNull(idx + 1) ?: 0.0
                            val diff = currGrade - prevGrade
                            val color = when {
                                diff > 0.1 -> Color(0xFF43CEA2)
                                diff < -0.1 -> Color(0xFFD32F2F)
                                else -> Color(0xFF185A9D)
                            }
                            drawLine(
                                color = color,
                                start = androidx.compose.ui.geometry.Offset(width * (idx + 1) / (periodNames.size + 1).toFloat(), height * (1 - prevGrade.toFloat() / 4f)),
                                end = androidx.compose.ui.geometry.Offset(width * (idx + 2) / (periodNames.size + 1).toFloat(), height * (1 - currGrade.toFloat() / 4f)),
                                strokeWidth = 6f
                            )
                        }
                    }
                    Text(
                        text = "Prelim → Midterm → Final",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = true, onCheckedChange = {}, enabled = false)
                    Text("Improvement needed in Finals", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
        // Subject Performance Comparison Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Subject Performance Comparison", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    subjectPerformances.forEach { (subject, grade) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .height(72.dp * (grade.toFloat() / 4f))
                                    .width(32.dp)
                                    .background(when {
                                        grade >= 3.0 -> Color(0xFF43CEA2)
                                        grade >= 2.0 -> Color(0xFFFFC107)
                                        else -> Color(0xFFD32F2F)
                                    }, RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(subject, fontSize = 12.sp)
                            Text(grade.toString(), fontWeight = FontWeight.Bold, color = when {
                                grade >= 3.0 -> Color(0xFF43CEA2)
                                grade >= 2.0 -> Color(0xFFFFC107)
                                else -> Color(0xFFD32F2F)
                            }, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        // Performance Insights Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("\uD83D\uDCCA", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Performance Insights", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    periodNames.zipWithNext().forEachIndexed { idx, (prev, curr) ->
                        val prevGrade = periodGrades.getOrNull(idx) ?: 0.0
                        val currGrade = periodGrades.getOrNull(idx + 1) ?: 0.0
                        val diff = currGrade - prevGrade
                        val color = when {
                            diff > 0.1 -> Color(0xFF43CEA2)
                            diff < -0.1 -> Color(0xFFD32F2F)
                            else -> Color(0xFF185A9D)
                        }
                        Text(
                            "• $curr grade ${(if (diff > 0) "improved" else if (diff < 0) "dropped" else "remained stable")} by ${String.format("%.2f", kotlin.math.abs(diff))} from $prev",
                            fontSize = 14.sp,
                            color = color
                        )
                    }
                    Text(
                        "• ${atRiskSubjects.size} subject(s) currently at risk",
                        fontSize = 14.sp,
                        color = if (atRiskSubjects.isEmpty()) Color(0xFF43CEA2) else Color(0xFFD32F2F)
                    )
                    val onTrackCount = subjects.count { it.status == SubjectStatus.ON_TRACK }
                    Text(
                        "• $onTrackCount subject(s) on track to meet target grades",
                        fontSize = 14.sp,
                        color = Color(0xFF43CEA2)
                    )
                }
            }
        }
    }
}