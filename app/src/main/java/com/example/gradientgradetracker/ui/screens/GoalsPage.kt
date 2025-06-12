package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip

@Composable
fun GoalPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
            .padding(bottom = 16.dp)
    ) {
        // Academic Goals Section
        Text(
            text = "🎯 Academic Goals",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        // Statistics Target Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Statistics Target", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("2.0", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2E7D32))
                }
                Text("Current: 1.8 | Need: +0.2 improvement", fontSize = 14.sp, color = Color(0xFF388E3C))
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.9f,
                    color = Color(0xFFD32F2F),
                    trackColor = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
        // Overall GWA Target Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F4EA)),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Overall GWA Target", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("2.5", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2E7D32))
                }
                Text("Current: 2.85 | Target exceeded! 🎉", fontSize = 14.sp, color = Color(0xFF388E3C))
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 1.0f,
                    color = Color(0xFF00BCD4),
                    trackColor = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
        // Alerts & Reminders Section
        Text(
            text = "\uD83D\uDD14 Alerts & Reminders",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
        )
        // Upcoming Assessment Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("\uD83D\uDCDD Upcoming Assessment", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                Text("Statistics Final Exam in 3 days (March 20, 2024). You need 2.8 to pass.", fontSize = 14.sp)
            }
        }
        // Grade Risk Alert Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("\u26A0\uFE0F Grade Risk Alert", fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                Text("Statistics grade below passing. Schedule study sessions immediately.", fontSize = 14.sp)
            }
        }
        // Missing Assignment Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("\uD83D\uDCC4 Missing Assignment", fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                Text("Database Assignment 2 due tomorrow. Submit to maintain grade standing.", fontSize = 14.sp)
            }
        }
        // Achievement Unlocked Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("\uD83C\uDFC6 Achievement Unlocked", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                Text("Programming grade improved for 3 consecutive periods!", fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Set New Goal Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Button(
                onClick = { /* TODO: Handle set new goal */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF43CEA2), Color(0xFF185A9D))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Set New Goal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
} 