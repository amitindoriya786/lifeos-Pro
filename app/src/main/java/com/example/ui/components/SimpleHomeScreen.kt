package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeOSState
import com.example.data.model.TaskItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun SimpleHomeScreen(
    state: LifeOSState,
    onNavigateTab: (String) -> Unit,
    onToggleTask: (Long) -> Unit,
    onSprintTrigger: () -> Unit,
    onOpenPrimeModal: (String) -> Unit,
    onSendNotificationTest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val q1OrP1Tasks = state.tasks.filter { it.quadrant == "Q1" || it.priority == "P1" }
    val nextMustDo = q1OrP1Tasks.firstOrNull { !it.done } ?: state.tasks.firstOrNull { !it.done }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("simple_home_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
    ) {
        // Welcoming & Clarity Header (Simple & Not Messy)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Welcome to LifeOS Pro",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Where would you like to focus right now?",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(EmeraldGlow)
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Score: ${state.profile.disciplineScore}/100",
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 90-Day North Star / Morning Check-In Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D0F17))
                            .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { onOpenPrimeModal("morning") }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("home_morning_checkin_pill")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (state.profile.morningPrime.isNotBlank())
                                        "Today's Goal: ${state.profile.morningPrime}"
                                    else
                                        "Tap to do Morning Check-In & Map to Matrix",
                                    color = AmberLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AmberGlow)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CHECK IN ☀️",
                                    color = AmberAccent,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // TOP 3 PRIORITY GOALS (COUNTDOWN & DEADLINES) - User Requested
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("top_3_goals_countdown_card")
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
                                imageVector = Icons.Default.TrackChanges,
                                contentDescription = null,
                                tint = IndigoLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "TOP 3 PRIORITY GOALS (DAYS LEFT)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        TextButton(
                            onClick = { onNavigateTab("future_goals") },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Full Workflow", color = IndigoLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = IndigoLight, modifier = Modifier.size(14.dp))
                        }
                    }

                    val top3Goals = state.futureGoals.take(3)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val now = System.currentTimeMillis()

                    if (top3Goals.isNotEmpty()) {
                        top3Goals.forEachIndexed { index, goal ->
                            val daysLeft = try {
                                val parsedDate = dateFormat.parse(goal.targetDate)
                                if (parsedDate != null) {
                                    val diff = parsedDate.time - now
                                    TimeUnit.MILLISECONDS.toDays(diff)
                                } else null
                            } catch (e: Exception) { null }

                            val priorityLabel = when (index) {
                                0 -> "#1 TOP PRIORITY"
                                1 -> "#2 PRIORITY"
                                else -> "#3 PRIORITY"
                            }
                            val priorityColor = when (index) {
                                0 -> AmberAccent
                                1 -> IndigoLight
                                else -> EmeraldLight
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateTab("future_goals") }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(priorityColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .border(1.dp, priorityColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(priorityLabel, color = priorityColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .background(LifeOSSurfaceElevated, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(goal.category, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }

                                        // Real-Time Countdown Badge
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (daysLeft != null && daysLeft <= 45) RoseAccent.copy(alpha = 0.2f)
                                                    else EmeraldPrimary.copy(alpha = 0.15f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (daysLeft != null && daysLeft <= 45) RoseAccent.copy(alpha = 0.5f)
                                                    else EmeraldPrimary.copy(alpha = 0.4f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = if (daysLeft != null) {
                                                    if (daysLeft > 0) "⚡ $daysLeft Days Left" else "⏰ Target Due Today"
                                                } else "🎯 Active Target",
                                                color = if (daysLeft != null && daysLeft <= 45) RoseLight else EmeraldLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    Text(
                                        text = goal.title,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Target Ending: ${goal.targetDate} (${goal.targetHorizon})",
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                        Text(
                                            text = "${goal.currentProgress}% / Ideal ${goal.idealProgress}%",
                                            color = if (goal.currentProgress >= goal.idealProgress) EmeraldLight else AmberLight,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { goal.currentProgress / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = priorityColor,
                                        trackColor = LifeOSBorder
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No future goals logged yet. Tap + to set your top 3 targets.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Phone Notifications & Study Alert Quick Trigger
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "Phone Notifications Active",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Direct alerts for tests, revisions & deadlines",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Button(
                        onClick = onSendNotificationTest,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary.copy(alpha = 0.2f),
                            contentColor = EmeraldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Test Alert", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Visual Chart: Study Modules & Syllabus Completion Rates (Dashboard Visual Summary)
        item {
            StudyCompletionChartCard(
                state = state,
                onNavigateStudy = { onNavigateTab("study") }
            )
        }

        // Direct Action Cards ("Kisme jaana hai" made simple and intuitive)
        item {
            Text(
                text = "SELECT YOUR WORKSPACE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }

        // 1. Study Hub & Real-Time Gemini AI Card (User Requested Core Feature)
        item {
            WorkspaceLaunchCard(
                title = "Study Hub & Real-Time Gemini AI",
                subtitle = "Tests deep report, books/modules solved, lectures, syllabus tracker & AI revision advisor.",
                badgeText = "${state.tests.size} Tests | ${state.syllabusChapters.count { it.theoryCompleted }} Ch Done",
                badgeColor = IndigoAccent,
                icon = Icons.Default.School,
                iconTint = IndigoLight,
                onClick = { onNavigateTab("study") }
            )
        }

        // 2. Eisenhower Matrix Card
        item {
            WorkspaceLaunchCard(
                title = "Eisenhower Matrix (4 Quadrants)",
                subtitle = "Prioritize tasks by Urgent vs Important. Focus on Q2 Top 1% strategic work.",
                badgeText = "${state.tasks.count { !it.done }} Pending Tasks",
                badgeColor = RoseAccent,
                icon = Icons.Default.GridView,
                iconTint = RoseAccent,
                onClick = { onNavigateTab("matrix") }
            )
        }

        // 2. Future Goals vs Ideal Benchmark Card
        item {
            WorkspaceLaunchCard(
                title = "Future Goals & Ideal Benchmark",
                subtitle = "Compare your actual pace side-by-side with Top 1% benchmark trajectories.",
                badgeText = "${state.futureGoals.size} Active Goals",
                badgeColor = IndigoAccent,
                icon = Icons.Default.TrackChanges,
                iconTint = IndigoLight,
                onClick = { onNavigateTab("future_goals") }
            )
        }

        // 3. Focus & Feynman Timer Card
        item {
            WorkspaceLaunchCard(
                title = "Focus & Feynman Timer",
                subtitle = "Deep work sessions, Pomodoro timers, and 3-min Feynman teaching drills.",
                badgeText = "Pomodoro / Deep",
                badgeColor = EmeraldPrimary,
                icon = Icons.Default.Timer,
                iconTint = EmeraldLight,
                onClick = { onNavigateTab("focus") }
            )
        }

        // 4. 3-Pillar Mastery (Academic, Business, Personal)
        item {
            WorkspaceLaunchCard(
                title = "3-Pillars & Analytics",
                subtitle = "Academic syllabus, business leverage ratio, safe spend, and 10k hr rank.",
                badgeText = "3 Pillars Active",
                badgeColor = AmberAccent,
                icon = Icons.Default.Layers,
                iconTint = AmberAccent,
                onClick = { onNavigateTab("pillars") }
            )
        }

        // Must-Do Right Now Spotlight
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f)),
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
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "CURRENT #1 MUST-DO TASK",
                                color = EmeraldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Button(
                            onClick = onSprintTrigger,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text("2-Min Start", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    if (nextMustDo != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F17), RoundedCornerShape(10.dp))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = nextMustDo.done,
                                onCheckedChange = { onToggleTask(nextMustDo.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = EmeraldPrimary,
                                    checkmarkColor = Color.Black,
                                    uncheckedColor = LifeOSBorder
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nextMustDo.title,
                                    color = if (nextMustDo.done) TextMuted else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textDecoration = if (nextMustDo.done) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Row(
                                    modifier = Modifier.padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "[${nextMustDo.quadrant}] ${nextMustDo.priority}",
                                        color = RoseAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (nextMustDo.leverage == "10k") "₹10k/hr Leverage" else "₹100/hr Admin",
                                        color = if (nextMustDo.leverage == "10k") EmeraldLight else TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "All P1 & Q1 tasks completed! Ready for Q2 high-leverage work.",
                            color = EmeraldLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Morning Prime & Night Brain-Dump Shortcuts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onOpenPrimeModal("morning") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LifeOSSurface,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_morning_checkin_button")
                ) {
                    Icon(Icons.Default.WbSunny, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Morning Check-In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onOpenPrimeModal("night") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LifeOSSurface,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = IndigoAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Night Offload", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WorkspaceLaunchCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f))
                    .border(1.dp, iconTint.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
