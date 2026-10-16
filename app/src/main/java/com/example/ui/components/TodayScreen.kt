package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitItem
import com.example.data.model.LifeOSState
import com.example.data.model.SpacedRepetitionItem
import com.example.data.model.TaskItem
import com.example.ui.theme.*

@Composable
fun TodayScreen(
    state: LifeOSState,
    timerMode: String,
    timerSeconds: Int,
    isTimerRunning: Boolean,
    energyFilter: String,
    onTimerModeChange: (String) -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onLogWastedClick: () -> Unit,
    onSaveFeynmanGap: (String) -> Unit,
    onToggleTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onAddTask: (String, String, String, String) -> Unit,
    onAutoReschedule: () -> Unit,
    onToggleHabit: (Long) -> Unit,
    onCompleteSpaced: (Long) -> Unit,
    onOpenAddSpaced: () -> Unit,
    onOpenAddHabit: () -> Unit,
    onOpenPrimeModal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf("P1") }
    var newTaskLeverage by remember { mutableStateOf("10k") }
    var feynmanGapInput by remember { mutableStateOf("") }

    val p1DoneCount = state.tasks.count { it.priority == "P1" && it.done }
    val p3Locked = state.config.p3LockEnabled && p1DoneCount == 0

    val filteredTasks = remember(state.tasks, energyFilter) {
        if (energyFilter == "all") state.tasks
        else state.tasks.filter { it.energy == energyFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("today_screen_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // 1. Morning Prime & Night Brain-Dump Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Morning Prime Card
                Surface(
                    onClick = { onOpenPrimeModal("morning") },
                    shape = RoundedCornerShape(14.dp),
                    color = LifeOSSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("morning_prime_card")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Morning Prime",
                                tint = AmberAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Morning Prime",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "90-Day North Star",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Night Brain-Dump Card
                Surface(
                    onClick = { onOpenPrimeModal("night") },
                    shape = RoundedCornerShape(14.dp),
                    color = LifeOSSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("night_dump_card")
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IndigoGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = "Night Brain-Dump",
                                tint = IndigoAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Night Brain-Dump",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Clear mental clutter",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 2. 3-Mode Focus & Feynman Timer Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("focus_timer_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header & Mode Selectors
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
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "FOCUS & MASTERY TIMER",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Mode Pills
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            listOf(
                                "pomodoro" to "Pomodoro",
                                "deep" to "Deep SW",
                                "feynman" to "Feynman 3m"
                            ).forEach { (m, label) ->
                                val active = timerMode == m
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) EmeraldPrimary else Color.Transparent)
                                        .clickable { onTimerModeChange(m) }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (active) Color.Black else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Large Time Display & Controls
                    val minutes = timerSeconds / 60
                    val seconds = timerSeconds % 60
                    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = timeFormatted,
                                color = Color.White,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = when (timerMode) {
                                    "pomodoro" -> "Deep Focus Session • +₹10k/hr Leverage"
                                    "deep" -> "Open-Ended Deep Work Stopwatch"
                                    else -> "Feynman 3-Min Concept Delivery Drill"
                                },
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Start / Pause
                            Button(
                                onClick = onToggleTimer,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("timer_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isTimerRunning) "Pause" else "Start",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTimerRunning) "PAUSE" else "START",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }

                            // Reset
                            IconButton(
                                onClick = onResetTimer,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(LifeOSSurfaceElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, LifeOSBorder, RoundedCornerShape(10.dp))
                                    .testTag("timer_reset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset Timer",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Log Wasted Time
                            IconButton(
                                onClick = onLogWastedClick,
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(RoseGlow, RoundedCornerShape(10.dp))
                                    .border(1.dp, RoseAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .testTag("log_wasted_time_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Log Distraction",
                                    tint = RoseAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // Feynman Knowledge Gap Input (Visible in Feynman mode)
                    if (timerMode == "feynman") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(IndigoGlow, RoundedCornerShape(10.dp))
                                .border(1.dp, IndigoAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "🎙 Explain Concept Out Loud (Like Teaching a 10-Yr Old):",
                                color = IndigoLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = feynmanGapInput,
                                    onValueChange = { feynmanGapInput = it },
                                    placeholder = { Text("Where did you get stuck? (Tag Gap)", fontSize = 11.sp, color = TextMuted) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = IndigoAccent,
                                        unfocusedBorderColor = LifeOSBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color(0xFF0D0F17),
                                        unfocusedContainerColor = Color(0xFF0D0F17)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                                )
                                Button(
                                    onClick = {
                                        if (feynmanGapInput.isNotBlank()) {
                                            onSaveFeynmanGap(feynmanGapInput.trim())
                                            feynmanGapInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = IndigoAccent,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("Tag Gap", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Daily Priority Matrix (P1, P2, P3 with P3 Lock & Auto-Reschedule)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("priority_matrix_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header & Auto-Reschedule
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "DAILY PRIORITY MATRIX",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (p3Locked) "🔒 Complete/Start 1 P1 task to unlock P3 tasks" else "✅ P1 Active — All Priority Levels Unlocked",
                                color = if (p3Locked) AmberAccent else EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = onAutoReschedule,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = IndigoGlow,
                                contentColor = IndigoLight
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(IndigoAccent.copy(alpha = 0.35f))
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(28.dp)
                                .testTag("auto_reschedule_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Auto-Reschedule", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Quick Add Task Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            placeholder = { Text("Add target task...", fontSize = 11.sp, color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = LifeOSBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0D0F17),
                                unfocusedContainerColor = Color(0xFF0D0F17)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_task_input"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newTaskTitle.isNotBlank()) {
                                    val energy = if (newTaskPriority == "P1") "high" else "med"
                                    onAddTask(newTaskTitle.trim(), newTaskPriority, newTaskLeverage, energy)
                                    newTaskTitle = ""
                                }
                            })
                        )

                        // Priority Selector Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D0F17))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    newTaskPriority = when (newTaskPriority) {
                                        "P1" -> "P2"
                                        "P2" -> "P3"
                                        else -> "P1"
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = newTaskPriority,
                                color = when (newTaskPriority) {
                                    "P1" -> RoseAccent
                                    "P2" -> AmberAccent
                                    else -> TextSecondary
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Leverage Selector Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D0F17))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    newTaskLeverage = if (newTaskLeverage == "10k") "100" else "10k"
                                }
                                .padding(horizontal = 6.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (newTaskLeverage == "10k") "₹10k" else "₹100",
                                color = if (newTaskLeverage == "10k") EmeraldPrimary else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Add Button
                        IconButton(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    val energy = if (newTaskPriority == "P1") "high" else "med"
                                    onAddTask(newTaskTitle.trim(), newTaskPriority, newTaskLeverage, energy)
                                    newTaskTitle = ""
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .background(EmeraldPrimary, RoundedCornerShape(8.dp))
                                .testTag("add_task_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Task",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Task Items
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredTasks.forEach { task ->
                            val isLocked = p3Locked && task.priority == "P3"
                            TaskRowItem(
                                task = task,
                                isLocked = isLocked,
                                onToggle = { onToggleTask(task.id) },
                                onDelete = { onDeleteTask(task.id) }
                            )
                        }
                    }
                }
            }
        }

        // 4. Spaced Repetition Auto-Queue (1-3-7-21-45 Day Rule)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("spaced_repetition_card")
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
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = IndigoAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SPACED REPETITION QUEUE",
                                color = IndigoLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        TextButton(
                            onClick = onOpenAddSpaced,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "+ Log Concept/Topic",
                                color = EmeraldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.spacedQueue.isEmpty()) {
                        Text(
                            text = "No pending topics in spaced repetition queue.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.spacedQueue.forEach { s ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0D0F17), RoundedCornerShape(10.dp))
                                        .border(1.dp, LifeOSBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = s.topic,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${s.stage} • Due: ${s.due}",
                                            color = IndigoLight,
                                            fontSize = 9.sp
                                        )
                                    }

                                    Button(
                                        onClick = { onCompleteSpaced(s.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmeraldGlow,
                                            contentColor = EmeraldLight
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Reviewed", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Daily Habit Tracker (with good/bad correlation verdict)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habits_card")
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
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "HABIT INTELLIGENCE TRACKER",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        TextButton(
                            onClick = onOpenAddHabit,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("+ Add Habit", color = EmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.habits.forEach { habit ->
                            HabitRowItem(
                                habit = habit,
                                onToggle = { onToggleHabit(habit.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRowItem(
    task: TaskItem,
    isLocked: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D0F17))
            .border(
                1.dp,
                if (task.done) EmeraldPrimary.copy(alpha = 0.3f) else LifeOSBorder,
                RoundedCornerShape(10.dp)
            )
            .alpha(if (isLocked) 0.45f else 1f)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "P3 Locked",
                    tint = AmberAccent,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Checkbox(
                    checked = task.done,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmeraldPrimary,
                        checkmarkColor = Color.Black,
                        uncheckedColor = LifeOSBorder
                    ),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.done) TextMuted else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority Badge
                    Box(
                        modifier = Modifier
                            .background(
                                when (task.priority) {
                                    "P1" -> RoseGlow
                                    "P2" -> AmberGlow
                                    else -> Color(0xFF1E212D)
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                when (task.priority) {
                                    "P1" -> RoseAccent.copy(alpha = 0.4f)
                                    "P2" -> AmberAccent.copy(alpha = 0.4f)
                                    else -> LifeOSBorder
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = task.priority,
                            color = when (task.priority) {
                                "P1" -> RoseAccent
                                "P2" -> AmberAccent
                                else -> TextSecondary
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Leverage Badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (task.leverage == "10k") EmeraldGlow else Color(0xFF1A1D26),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (task.leverage == "10k") "₹10k/hr" else "₹100/hr",
                            color = if (task.leverage == "10k") EmeraldLight else TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Energy Badge
                    Text(
                        text = when (task.energy) {
                            "low" -> "⚡ Low"
                            "med" -> "⚡⚡ Med"
                            else -> "⚡⚡⚡ High"
                        },
                        color = TextMuted,
                        fontSize = 8.sp
                    )
                }
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete Task",
                tint = TextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun HabitRowItem(
    habit: HabitItem,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0D0F17))
            .border(
                1.dp,
                if (habit.doneToday) EmeraldPrimary.copy(alpha = 0.4f) else LifeOSBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onToggle() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = habit.name,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(
                            if (habit.type == "good") EmeraldGlow else RoseGlow,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (habit.type == "good") "GOOD HABIT" else "BAD HABIT",
                        color = if (habit.type == "good") EmeraldLight else RoseLight,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Correlation Verdict: ${habit.impact}",
                color = if (habit.type == "good") EmeraldLight else RoseLight,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = if (habit.doneToday) "✔ Logged" else "Tap",
                color = if (habit.doneToday) EmeraldPrimary else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Streak: ${habit.streak}d",
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
