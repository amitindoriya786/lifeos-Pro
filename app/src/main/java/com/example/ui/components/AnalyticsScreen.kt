package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeOSState
import com.example.ui.theme.*
import kotlin.math.ceil

@Composable
fun AnalyticsScreen(
    state: LifeOSState,
    onAddMasteryHours: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val masteryHours = state.profile.masteryHours
    val masteryPct = (masteryHours.toFloat() / 10000f).coerceIn(0f, 1f)
    val wastedHours = state.profile.wastedHoursWeek
    val focusedHours = state.profile.focusedHoursWeek
    val routineHours = 8.5
    val chaptersLost = ceil(wastedHours / 2.0).toInt().coerceAtLeast(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("analytics_screen_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // 1. 10,000-Hour Mastery Counter
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mastery_counter_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "10,000-HR TOP 1% MASTERY RANK",
                                color = AmberAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = "$masteryHours / 10,000 hrs",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFF0D0F17))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(masteryPct)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(AmberAccent, EmeraldPrimary)
                                    )
                                )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Rank: Specialist Tier",
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Next Rank: 2,500 hrs (Authority)",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }

                        Button(
                            onClick = { onAddMasteryHours(10) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberGlow,
                                contentColor = AmberLight
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("10 hrs", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Weekly Time-Flow Breakdown Chart
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "WEEKLY TIME-FLOW BREAKDOWN",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(RoseGlow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Waste Cost: ~$chaptersLost Modules Lost",
                                color = RoseAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Custom Ring / Arc Chart
                    val totalHours = (focusedHours + routineHours + wastedHours).coerceAtLeast(1.0)
                    val focusedRatio = (focusedHours / totalHours).toFloat()
                    val routineRatio = (routineHours / totalHours).toFloat()
                    val wastedRatio = (wastedHours / totalHours).toFloat()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(110.dp)) {
                            val strokeWidth = 18.dp.toPx()
                            val diameter = size.minDimension - strokeWidth
                            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                            val arcSize = Size(diameter, diameter)

                            var startAngle = -90f
                            val sweepFocused = focusedRatio * 360f
                            val sweepRoutine = routineRatio * 360f
                            val sweepWasted = wastedRatio * 360f

                            // Focused (Emerald)
                            drawArc(
                                color = EmeraldPrimary,
                                startAngle = startAngle,
                                sweepAngle = sweepFocused - 2f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweepFocused

                            // Routine (Amber)
                            drawArc(
                                color = AmberAccent,
                                startAngle = startAngle,
                                sweepAngle = sweepRoutine - 2f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            startAngle += sweepRoutine

                            // Wasted (Rose)
                            drawArc(
                                color = RoseAccent,
                                startAngle = startAngle,
                                sweepAngle = sweepWasted - 2f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${String.format("%.1f", focusedHours)}h",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Deep Focus",
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        LegendItem(color = EmeraldPrimary, label = "Deep Focus (${String.format("%.1f", focusedHours)}h)")
                        LegendItem(color = AmberAccent, label = "Admin (${routineHours}h)")
                        LegendItem(color = RoseAccent, label = "Wasted (${String.format("%.1f", wastedHours)}h)")
                    }
                }
            }
        }

        // 3. Target Trajectory Simulator
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TARGET TRAJECTORY SIMULATOR",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "+25m/day = Finish 6d Early",
                            color = EmeraldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            val idealPoints = listOf(0.15f, 0.32f, 0.50f, 0.68f, 0.85f, 1.0f)
                            val actualPoints = listOf(0.14f, 0.29f, 0.46f, 0.65f, 0.86f, 1.0f)
                            val stepX = w / (idealPoints.size - 1)

                            // Draw grid lines
                            drawLine(Color(0xFF1F2433), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = 1f)

                            // Ideal line (Indigo)
                            val idealPath = Path()
                            idealPoints.forEachIndexed { i, pt ->
                                val x = i * stepX
                                val y = h - (pt * h * 0.9f)
                                if (i == 0) idealPath.moveTo(x, y) else idealPath.lineTo(x, y)
                            }
                            drawPath(
                                path = idealPath,
                                color = IndigoAccent,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Actual boosted line (Emerald)
                            val actualPath = Path()
                            actualPoints.forEachIndexed { i, pt ->
                                val x = i * stepX
                                val y = h - (pt * h * 0.9f)
                                if (i == 0) actualPath.moveTo(x, y) else actualPath.lineTo(x, y)
                                drawCircle(EmeraldPrimary, radius = 3.dp.toPx(), center = Offset(x, y))
                            }
                            drawPath(
                                path = actualPath,
                                color = EmeraldPrimary,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "• Ideal Planned Path (Purple)", color = IndigoLight, fontSize = 9.sp)
                        Text(text = "• Your Boosted Pace (Green)", color = EmeraldLight, fontSize = 9.sp)
                    }
                }
            }
        }

        // 4. Sunday Night CEO Weekly Review Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = IndigoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AUTOMATED SUNDAY CEO REPORT",
                                color = IndigoLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(EmeraldPrimary, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Grade: A-",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "• Top Win: 84% P1 Task Execution & 18.5 hrs Deep Focus logged.",
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "• Primary Time Leak: 2:00 PM – 4:00 PM Afternoon Slump (3.2 hrs lost).",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "• Habit Verdict: Morning Prime boosted daily completion by +34%.",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(text = label, color = TextSecondary, fontSize = 9.sp)
    }
}
