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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import com.example.gradientgradetracker.ui.screens.OverviewTab
import com.example.gradientgradetracker.ui.screens.SubjectsTab
import com.example.gradientgradetracker.ui.screens.AnalyticsTab
import com.example.gradientgradetracker.ui.screens.GoalPage

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
                2 -> "Performance Analytics" to "Visual insights into your academic progress"
                3 -> "Goals" to "Manage your Goals"
                else -> "" to ""
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
            3 -> GoalPage()
        }
    }
} 