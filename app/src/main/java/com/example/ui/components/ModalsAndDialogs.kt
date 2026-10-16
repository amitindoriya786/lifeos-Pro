package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun MorningCheckInDialog(
    initialGoal: String,
    onDismiss: () -> Unit,
    onConfirm: (primaryGoal: String, quadrant: String, energy: String, leverage: String) -> Unit
) {
    var primaryGoal by remember(initialGoal) { mutableStateOf(initialGoal) }
    var selectedQuadrant by remember { mutableStateOf("Q1") } // "Q1" (Do First) or "Q2" (Strategic 10k)
    var selectedEnergy by remember { mutableStateOf("high") } // "high", "med", "low"
    var selectedDuration by remember { mutableStateOf("2.5 hrs") }
    var selectedLeverage by remember { mutableStateOf("10k") }

    val inspirationGoals = listOf(
        "Solve 45 Physics Rotational Dynamics PYQs",
        "Complete Full 3-Hour Timed Mock Test #4",
        "Deep Revision: Organic Reactions & Mechanisms",
        "Master Chapter 6 Calculus Problem Set"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberAccent.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("morning_checkin_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberGlow)
                                .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Morning Sun",
                                tint = AmberAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "DAILY MORNING CHECK-IN",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Peak Intent & Matrix Alignment",
                                color = AmberLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Subtitle Instruction
                Text(
                    text = "What is your #1 non-negotiable primary goal for today? Once defined, it will automatically map directly into your top-level Eisenhower Matrix priority.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                // Input Field
                OutlinedTextField(
                    value = primaryGoal,
                    onValueChange = { primaryGoal = it },
                    placeholder = {
                        Text(
                            "e.g. Solve 50 Physics PYQs & review error notes",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberAccent,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("morning_goal_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium)
                )

                // Quick Inspiration Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "QUICK INSPIRATION (TAP TO SET)",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    inspirationGoals.forEach { insp ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0A0C14))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .clickable { primaryGoal = insp }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(12.dp))
                                Text(
                                    text = insp,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Automatic Eisenhower Matrix Priority Mapping Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("matrix_mapping_preview_card")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldGlow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.GridView, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(12.dp))
                            }
                            Text(
                                text = "AUTOMATIC MATRIX MAPPING",
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = "Maps automatically as Task #1 in your Eisenhower Matrix with top priority:",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )

                        // Quadrant Selection Pill Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Q1 Choice
                            val isQ1 = selectedQuadrant == "Q1"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isQ1) RoseAccent.copy(alpha = 0.2f) else Color(0xFF141722))
                                    .border(1.dp, if (isQ1) RoseAccent else LifeOSBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedQuadrant = "Q1" }
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Q1: Do First",
                                            color = if (isQ1) RoseAccent else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isQ1) {
                                            Text(text = "★ TOP", color = RoseAccent, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                    Text(
                                        text = "Urgent & Important (P1)",
                                        color = TextSecondary,
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            // Q2 Choice
                            val isQ2 = selectedQuadrant == "Q2"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isQ2) EmeraldPrimary.copy(alpha = 0.2f) else Color(0xFF141722))
                                    .border(1.dp, if (isQ2) EmeraldPrimary else LifeOSBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedQuadrant = "Q2" }
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "Q2: Strategic",
                                            color = if (isQ2) EmeraldLight else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isQ2) {
                                            Text(text = "★ 10K", color = EmeraldPrimary, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                    Text(
                                        text = "Not Urgent, Important (P1)",
                                        color = TextSecondary,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }

                        // Attributes row (Leverage + Energy + Duration)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF141722), RoundedCornerShape(8.dp))
                                    .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⚡ Leverage: \$10k", color = AmberLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF141722), RoundedCornerShape(8.dp))
                                    .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🔥 Peak Energy", color = RoseAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFF141722), RoundedCornerShape(8.dp))
                                    .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⏱ $selectedDuration", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Duration selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("1.5 hrs", "2.5 hrs", "3.5 hrs", "5.0 hrs").forEach { dur ->
                                val isSelected = selectedDuration == dur
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) AmberAccent.copy(alpha = 0.2f) else Color(0xFF141722))
                                        .border(1.dp, if (isSelected) AmberAccent else LifeOSBorder, RoundedCornerShape(6.dp))
                                        .clickable { selectedDuration = dur }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dur,
                                        color = if (isSelected) AmberAccent else TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Submit Button
                val isValid = primaryGoal.trim().isNotBlank()
                Button(
                    onClick = {
                        if (isValid) {
                            onConfirm(primaryGoal.trim(), selectedQuadrant, selectedEnergy, selectedLeverage)
                        }
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black,
                        disabledContainerColor = LifeOSBorder,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_morning_checkin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "☀️ Lock In & Map to Eisenhower Matrix",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip for Now", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun PrimeModalDialog(
    type: String, // "morning" or "night"
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember(initialValue) { mutableStateOf(initialValue) }
    val isMorning = type == "morning"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isMorning) AmberAccent.copy(alpha = 0.4f) else IndigoAccent.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("prime_modal_dialog")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isMorning) "☀️ Morning Prime (60-Sec Vision)" else "🌙 Night Brain-Dump & Offload",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Text(
                    text = if (isMorning) {
                        "Lock in your 90-Day North Star Goal & #1 Non-Negotiable Task before touching social media:"
                    } else {
                        "Offload all pending thoughts, worries, and tomorrow's top priorities for deep sleep:"
                    },
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    minLines = 4,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isMorning) AmberAccent else IndigoAccent,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                Button(
                    onClick = { onSave(text.trim()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMorning) EmeraldPrimary else IndigoAccent,
                        contentColor = if (isMorning) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save to Personal Growth Log", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LogWastedTimeDialog(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var hoursText by remember { mutableStateOf("1.0") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoseAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("log_wasted_dialog")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Log Distraction / Wasted Time",
                        color = RoseAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Text(
                    text = "Track scrolling, binge-watching, or unplanned rabbit holes to measure distraction cost:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it },
                    label = { Text("Hours Wasted (e.g. 1.5)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoseAccent,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )

                Button(
                    onClick = {
                        val h = hoursText.toDoubleOrNull() ?: 1.0
                        onSave(h)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoseAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Record Distraction Hours", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddSpacedTopicDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var topic by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add Topic to Spaced Repetition",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Text(
                    text = "This concept will auto-schedule into your 1-3-7-21-45 day review queue:",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    placeholder = { Text("e.g. Magnetism & Biot-Savart Law", color = TextMuted, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                Button(
                    onClick = {
                        if (topic.isNotBlank()) onSave(topic.trim())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Auto-Schedule Reviews", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }
    var habitType by remember { mutableStateOf("good") }
    var habitImpact by remember { mutableStateOf("+30% Focus Efficiency") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Add Habit Intelligence Rule", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    placeholder = { Text("Habit name (e.g. 20m Morning Sun Exposure)", color = TextMuted, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (habitType == "good") EmeraldPrimary else Color(0xFF0D0F17))
                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                            .clickable { habitType = "good" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Good Habit (+)", color = if (habitType == "good") Color.Black else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (habitType == "bad") RoseAccent else Color(0xFF0D0F17))
                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                            .clickable { habitType = "bad" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Bad Habit (-)", color = if (habitType == "bad") Color.White else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = habitImpact,
                    onValueChange = { habitImpact = it },
                    label = { Text("Correlation Verdict", fontSize = 10.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
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
                        if (habitName.isNotBlank()) onSave(habitName.trim(), habitType, habitImpact.trim())
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Habit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExportJsonDialog(
    jsonText: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Export Database Snapshot", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = jsonText,
                        color = EmeraldLight,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddFutureGoalDialog(
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        category: String,
        horizon: String,
        targetDate: String,
        curMetric: String,
        idealMetric: String,
        curTraj: String,
        idealTraj: String,
        gap: String,
        bridge: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic") }
    var horizon by remember { mutableStateOf("90 Days") }
    var targetDate by remember { mutableStateOf("2026-12-31") }
    var curMetric by remember { mutableStateOf("2.5h / day content prep") }
    var idealMetric by remember { mutableStateOf("4.0h / day top 1% deep focus") }
    var curTraj by remember { mutableStateOf("14 months at current rate") }
    var idealTraj by remember { mutableStateOf("6.5 months top 1% benchmark") }
    var gap by remember { mutableStateOf("1.5h lost daily to unscheduled distractions") }
    var bridge by remember { mutableStateOf("Lock 3-hour morning deep work block with phone in another room") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("add_future_goal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Create Goal with Ideal Benchmark",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title", fontSize = 10.sp) },
                    placeholder = { Text("e.g. Master Electromagnetism & Advance 500 Problems", fontSize = 10.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoAccent,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                // Category & Horizon selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D0F17))
                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                category = when (category) {
                                    "Academic" -> "Business"
                                    "Business" -> "Personal"
                                    else -> "Academic"
                                }
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Cat: $category", color = IndigoLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0D0F17))
                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                horizon = when (horizon) {
                                    "90 Days" -> "6 Months"
                                    "6 Months" -> "1 Year"
                                    else -> "90 Days"
                                }
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Span: $horizon", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(text = "CURRENT WORKFLOW (ACTUAL):", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)

                OutlinedTextField(
                    value = curMetric,
                    onValueChange = { curMetric = it },
                    label = { Text("Your Daily Output / Pace", fontSize = 9.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                Text(text = "IDEAL BENCHMARK (TOP 1%):", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)

                OutlinedTextField(
                    value = idealMetric,
                    onValueChange = { idealMetric = it },
                    label = { Text("Ideal Benchmark Metric / Pace", fontSize = 9.sp) },
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
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                Text(text = "GAP ANALYSIS & BRIDGE ACTION:", color = RoseAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)

                OutlinedTextField(
                    value = gap,
                    onValueChange = { gap = it },
                    label = { Text("Where is the time or focus leaking?", fontSize = 9.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoseAccent,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                OutlinedTextField(
                    value = bridge,
                    onValueChange = { bridge = it },
                    label = { Text("Exact action rule to bridge the gap", fontSize = 9.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = LifeOSBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF0D0F17),
                        unfocusedContainerColor = Color(0xFF0D0F17)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(title.trim(), category, horizon, targetDate, curMetric, idealMetric, curTraj, idealTraj, gap, bridge)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Future Goal & Benchmark", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
