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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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
    var selectedNavIndex by remember { mutableStateOf(0) }
    val navItems = listOf(
        "Home" to Icons.Default.Home,
        "Grades" to Icons.Default.DateRange,
        "Reminders" to Icons.Default.Notifications,
        "Settings" to Icons.Default.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF185A9D),
                contentColor = Color.White
            ) {
                navItems.forEachIndexed { index, pair ->
                    NavigationBarItem(
                        selected = selectedNavIndex == index,
                        onClick = { selectedNavIndex = index },
                        icon = {
                            Icon(
                                imageVector = pair.second,
                                contentDescription = pair.first,
                                tint = if (selectedNavIndex == index) Color(0xFFFFC107) else Color.White
                            )
                        },
                        label = {
                            Text(
                                pair.first,
                                color = if (selectedNavIndex == index) Color(0xFFFFC107) else Color.White
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Top Bar
            val (title, subtitle) = when (selectedNavIndex) {
                0 -> "Academic Dashboard" to "Current Period: $currentPeriod - Week $week of $totalWeeks"
                1 -> "Grades" to "View and manage your grades"
                2 -> "Reminders" to "Stay on top of important dates"
                3 -> "Settings" to "Customize your experience"
                else -> "" to ""
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF185A9D))
                    .padding(top = 32.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

            // Content
            when (selectedNavIndex) {
                0 -> OverviewTab(gwa, subjectsAtRisk, passingRate, targetProgress, prelim, midterm, final)
                1 -> SubjectsTab()
                2 -> RemindersTab()
                3 -> SettingsTab()
            }
        }
    }
}