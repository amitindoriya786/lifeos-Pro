package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeOSState
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(
    state: LifeOSState,
    onSendBroadcast: (String) -> Unit,
    onUpdateConfig: (Boolean, Int, Int) -> Unit,
    onAddQuestion: (String, String) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    onResetDatabase: () -> Unit,
    onExportJson: () -> Unit,
    modifier: Modifier = Modifier
) {
    var broadcastMsg by remember { mutableStateOf("") }
    var p3Lock by remember(state.config.p3LockEnabled) { mutableStateOf(state.config.p3LockEnabled) }
    var pomodoroMins by remember(state.config.pomodoroMins) { mutableStateOf(state.config.pomodoroMins.toString()) }
    var savingsTarget by remember(state.config.savingsTarget) { mutableStateOf(state.config.savingsTarget.toString()) }

    var newQText by remember { mutableStateOf("") }
    var newQLevel by remember { mutableStateOf("Basic") }

    val doneTasks = state.tasks.count { it.done }
    val totalTasks = state.tasks.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("admin_panel_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
    ) {
        // Header & Actions
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ADMIN PANEL & BACKEND ENGINE",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Manage 20 Questions, Coaching Tone, and Live Telemetry",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = onExportJson,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Export", fontSize = 10.sp)
                            }

                            Button(
                                onClick = onResetDatabase,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RoseGlow,
                                    contentColor = RoseAccent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Reset DB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Admin KPI Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminKpiCard(
                        title = "Discipline Score",
                        value = "${state.profile.disciplineScore} / 100",
                        subtitle = "Tone: ${state.profile.coachTone.take(16)}..",
                        accentColor = EmeraldLight,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "Tasks & P1 Status",
                        value = "$doneTasks / $totalTasks Done",
                        subtitle = "Live Sync Active",
                        accentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminKpiCard(
                        title = "Focused vs Wasted",
                        value = "${state.profile.focusedHoursWeek}h / ${state.profile.wastedHoursWeek}h",
                        subtitle = "This Week Telemetry",
                        accentColor = IndigoLight,
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiCard(
                        title = "20-Q Diagnostic Bank",
                        value = "${state.questions.size} Active",
                        subtitle = "Basic • Inter • Adv",
                        accentColor = AmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Live HUD Push Notification Box
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = "LIVE HUD PUSH NOTIFICATION",
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Broadcast an instant high-priority alert to the top Mobile HUD banner:",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )

                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        placeholder = { Text("e.g. Coach Alert: Finish Rotational Dynamics P1 now!", fontSize = 11.sp, color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = LifeOSBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0D0F17),
                            unfocusedContainerColor = Color(0xFF0D0F17)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    Button(
                        onClick = {
                            if (broadcastMsg.isNotBlank()) {
                                onSendBroadcast(broadcastMsg.trim())
                                broadcastMsg = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Push Alert to Mobile Top HUD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // System Engine Rules
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "SYSTEM ENGINE RULES",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Strict P3 Lock toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Strict P3 Task Blur Lock", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Locks P3 tasks until 1 P1 task is done", color = TextMuted, fontSize = 9.sp)
                        }
                        Switch(
                            checked = p3Lock,
                            onCheckedChange = { p3Lock = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = EmeraldPrimary,
                                uncheckedTrackColor = LifeOSBorder
                            )
                        )
                    }

                    // Default Pomodoro Mins
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Default Pomodoro Duration (mins)", color = Color.White, fontSize = 11.sp)
                        OutlinedTextField(
                            value = pomodoroMins,
                            onValueChange = { pomodoroMins = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = LifeOSBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0D0F17),
                                unfocusedContainerColor = Color(0xFF0D0F17)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(70.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )
                    }

                    // Monthly Savings Target
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Monthly Savings Target (₹)", color = Color.White, fontSize = 11.sp)
                        OutlinedTextField(
                            value = savingsTarget,
                            onValueChange = { savingsTarget = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = LifeOSBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0D0F17),
                                unfocusedContainerColor = Color(0xFF0D0F17)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.width(95.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                        )
                    }

                    Button(
                        onClick = {
                            val p = pomodoroMins.toIntOrNull() ?: 45
                            val s = savingsTarget.toIntOrNull() ?: 45000
                            onUpdateConfig(p3Lock, p, s)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply System Engine Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 20-Q Questions Manager
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
                    Text(
                        text = "20 ONBOARDING DIAGNOSTIC QUESTIONS MANAGER",
                        color = IndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newQText,
                            onValueChange = { newQText = it },
                            placeholder = { Text("Add custom diagnostic question...", fontSize = 10.sp, color = TextMuted) },
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
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D0F17))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    newQLevel = when (newQLevel) {
                                        "Basic" -> "Intermediate"
                                        "Intermediate" -> "Advanced"
                                        else -> "Basic"
                                    }
                                }
                                .padding(horizontal = 6.dp, vertical = 12.dp)
                        ) {
                            Text(text = newQLevel, color = IndigoLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (newQText.isNotBlank()) {
                                    onAddQuestion(newQText.trim(), newQLevel)
                                    newQText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Q", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.questions.forEach { q ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                    .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(IndigoGlow, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = q.level, color = IndigoLight, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = q.question,
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteQuestion(q.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
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
private fun AdminKpiCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = title, color = TextMuted, fontSize = 9.sp)
            Text(text = value, color = accentColor, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(text = subtitle, color = TextSecondary, fontSize = 8.sp)
        }
    }
}
