package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeOSState
import com.example.ui.theme.*

@Composable
fun StudyCompletionChartCard(
    state: LifeOSState,
    onNavigateStudy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChartTab by remember { mutableStateOf("overview") } // "overview", "syllabus", "modules"

    // Calculation logic
    val totalSolvedQuestions = state.bookModules.sumOf { it.solvedCount }
    val totalQuestions = state.bookModules.sumOf { it.totalCount }.coerceAtLeast(1)
    val moduleQuestionsPct = (totalSolvedQuestions * 100 / totalQuestions).coerceIn(0, 100)
    val completedModulesCount = state.bookModules.count { it.status == "Completed" }

    val totalSyllabusChapters = state.syllabusChapters.size.coerceAtLeast(1)
    val theoryDoneChapters = state.syllabusChapters.count { it.theoryCompleted }
    val moduleDoneChapters = state.syllabusChapters.count { it.moduleSolved }
    val pyqDoneChapters = state.syllabusChapters.count { it.pyqSolved }

    val syllabusTheoryPct = (theoryDoneChapters * 100 / totalSyllabusChapters).coerceIn(0, 100)
    val syllabusComprehensivePct = ((theoryDoneChapters + moduleDoneChapters + pyqDoneChapters) * 100 / (totalSyllabusChapters * 3)).coerceIn(0, 100)

    val overallStudyRate = ((syllabusTheoryPct + moduleQuestionsPct) / 2).coerceIn(0, 100)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_completion_chart_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(EmeraldGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "STUDY & SYLLABUS COMPLETION",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Real-time module solving & topic mastery rates",
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }

                TextButton(
                    onClick = onNavigateStudy,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Study Hub", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                }
            }

            // Quick Chart Filter Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                    .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    "overview" to "Dual Gauge",
                    "syllabus" to "Syllabus Topics",
                    "modules" to "Book Modules"
                ).forEach { (key, label) ->
                    val isSelected = selectedChartTab == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                            .clickable { selectedChartTab = key }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = selectedChartTab,
                label = "chartViewAnimation"
            ) { view ->
                when (view) {
                    "overview" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Canvas Dual Gauge & Key Metrics
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D0F17), RoundedCornerShape(12.dp))
                                    .border(1.dp, LifeOSBorder, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Canvas Dual Donut Gauge
                                Box(
                                    modifier = Modifier.size(110.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeWidthOuter = 10.dp.toPx()
                                        val strokeWidthInner = 8.dp.toPx()

                                        // Outer track (Syllabus)
                                        drawArc(
                                            color = LifeOSBorder,
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidthOuter, cap = StrokeCap.Round)
                                        )

                                        // Outer Progress Arc (Syllabus Theory)
                                        drawArc(
                                            brush = Brush.sweepGradient(listOf(EmeraldPrimary, Color(0xFF34D399), EmeraldPrimary)),
                                            startAngle = -90f,
                                            sweepAngle = 360f * (syllabusTheoryPct / 100f),
                                            useCenter = false,
                                            style = Stroke(width = strokeWidthOuter, cap = StrokeCap.Round)
                                        )

                                        // Inner track (Modules)
                                        val innerInset = 16.dp.toPx()
                                        val innerSize = Size(size.width - innerInset * 2, size.height - innerInset * 2)
                                        val innerTopLeft = Offset(innerInset, innerInset)

                                        drawArc(
                                            color = LifeOSBorder.copy(alpha = 0.5f),
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            topLeft = innerTopLeft,
                                            size = innerSize,
                                            style = Stroke(width = strokeWidthInner, cap = StrokeCap.Round)
                                        )

                                        // Inner Progress Arc (Module Questions)
                                        drawArc(
                                            brush = Brush.sweepGradient(listOf(IndigoAccent, IndigoLight, IndigoAccent)),
                                            startAngle = -90f,
                                            sweepAngle = 360f * (moduleQuestionsPct / 100f),
                                            useCenter = false,
                                            topLeft = innerTopLeft,
                                            size = innerSize,
                                            style = Stroke(width = strokeWidthInner, cap = StrokeCap.Round)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$overallStudyRate%",
                                            color = Color.White,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "SYNC RATE",
                                            color = TextMuted,
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Legend & Metrics Breakdown
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Syllabus Metric
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldPrimary))
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Syllabus Done", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("$syllabusTheoryPct%", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                            }
                                            Text(
                                                "$theoryDoneChapters of $totalSyllabusChapters Chapters",
                                                color = TextMuted,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }

                                    // Module Metric
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IndigoLight))
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Modules Solved", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("$moduleQuestionsPct%", color = IndigoLight, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                            }
                                            Text(
                                                "$totalSolvedQuestions of $totalQuestions Questions",
                                                color = TextMuted,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }

                                    // PYQ Cleared Metric
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AmberAccent))
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("PYQs Mastered", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Text("${pyqDoneChapters * 100 / totalSyllabusChapters}%", color = AmberLight, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                            }
                                            Text(
                                                "$pyqDoneChapters Chapters with Archive Done",
                                                color = TextMuted,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Quick comparative visual stats pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                StatPill(
                                    label = "Completed Modules",
                                    value = "$completedModulesCount / ${state.bookModules.size}",
                                    color = IndigoLight,
                                    modifier = Modifier.weight(1f)
                                )
                                StatPill(
                                    label = "Comprehensive Mastery",
                                    value = "$syllabusComprehensivePct%",
                                    color = EmeraldLight,
                                    modifier = Modifier.weight(1f)
                                )
                                StatPill(
                                    label = "Avg Test Accuracy",
                                    value = "${if (state.tests.isNotEmpty()) state.tests.map { it.accuracyPct }.average().toInt() else 80}%",
                                    color = AmberLight,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    "syllabus" -> {
                        // Subject-Wise Syllabus Completion Rates
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val subjectsGrouped = state.syllabusChapters.groupBy { it.subject }

                            subjectsGrouped.forEach { (subject, chapters) ->
                                val doneCount = chapters.count { it.theoryCompleted }
                                val totalSubj = chapters.size.coerceAtLeast(1)
                                val pct = (doneCount * 100 / totalSubj)
                                val pyqCount = chapters.count { it.pyqSolved }

                                val subjectColor = when (subject) {
                                    "Physics" -> AmberAccent
                                    "Chemistry" -> IndigoLight
                                    "Maths" -> EmeraldPrimary
                                    else -> RoseAccent
                                }

                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(subjectColor)
                                                )
                                                Text(
                                                    text = subject,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                text = "$doneCount / $totalSubj Chapters ($pct%)",
                                                color = subjectColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }

                                        // Progress Bar
                                        LinearProgressIndicator(
                                            progress = { pct / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = subjectColor,
                                            trackColor = LifeOSBorder
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "PYQs Mastered: $pyqCount/$totalSubj",
                                                color = TextMuted,
                                                fontSize = 8.5.sp
                                            )
                                            Text(
                                                text = "${totalSubj - doneCount} remaining in syllabus",
                                                color = if (doneCount == totalSubj) EmeraldLight else TextSecondary,
                                                fontSize = 8.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "modules" -> {
                        // Book & Module Problem Solving Rates
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.bookModules.forEach { module ->
                                val pct = if (module.totalCount > 0) (module.solvedCount * 100 / module.totalCount).coerceIn(0, 100) else 0
                                val isDone = module.status == "Completed"

                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = module.bookName,
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${module.chapter} • ${module.exerciseName}",
                                                    color = TextSecondary,
                                                    fontSize = 8.5.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (isDone) EmeraldPrimary.copy(alpha = 0.2f) else IndigoAccent.copy(alpha = 0.2f),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "$pct% (${module.solvedCount}/${module.totalCount})",
                                                    color = if (isDone) EmeraldLight else IndigoLight,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }

                                        // Progress Bar
                                        LinearProgressIndicator(
                                            progress = { pct / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(5.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (isDone) EmeraldPrimary else IndigoLight,
                                            trackColor = LifeOSBorder
                                        )
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

@Composable
private fun StatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Text(label, color = TextMuted, fontSize = 7.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
