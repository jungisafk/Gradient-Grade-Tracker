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

@Composable
fun AnalyticsTab() {
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
                // Mocked Line Chart
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
                        drawLine(
                            color = Color(0xFF185A9D),
                            start = androidx.compose.ui.geometry.Offset(width * 0.1f, height * 0.6f),
                            end = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.6f),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = Color(0xFF43CEA2),
                            start = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.6f),
                            end = androidx.compose.ui.geometry.Offset(width * 0.9f, height * 0.3f),
                            strokeWidth = 6f
                        )
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .height(72.dp)
                                .width(32.dp)
                                .background(Color(0xFF43CEA2), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Programming", fontSize = 12.sp)
                        Text("1.2", fontWeight = FontWeight.Bold, color = Color(0xFF43CEA2), fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .width(32.dp)
                                .background(Color(0xFFFFC107), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Database", fontSize = 12.sp)
                        Text("2.3", fontWeight = FontWeight.Bold, color = Color(0xFFFFC107), fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .width(32.dp)
                                .background(Color(0xFFD32F2F), RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Statistics", fontSize = 12.sp)
                        Text("1.8", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 13.sp)
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
                    Text("• Statistics grade dropped 0.6 points from Prelim to Midterm", fontSize = 14.sp, color = Color(0xFFD32F2F))
                    Text("• Programming shows consistent improvement trend", fontSize = 14.sp, color = Color(0xFF43CEA2))
                    Text("• Focus on exam preparation for better Midterm performance", fontSize = 14.sp, color = Color(0xFF185A9D))
                    Text("• 2 subjects on track to meet target grades", fontSize = 14.sp, color = Color(0xFF43CEA2))
                }
            }
        }
    }
} 