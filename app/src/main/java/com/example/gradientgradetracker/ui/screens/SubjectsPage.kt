package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsTab() {
    var assessmentType by remember { mutableStateOf("Quiz") }
    val assessmentTypes = listOf("Quiz", "Exam", "Assignment", "Project")
    var scoreObtained by remember { mutableStateOf("") }
    var totalScore by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("03/15/2024") }
    var expanded by remember { mutableStateOf(false) }
    var assessments by remember { mutableStateOf(listOf<AssessmentEntry>()) }

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