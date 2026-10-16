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
import com.example.data.model.TargetDeadline
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun DeadlinesScreen(
    state: LifeOSState,
    onAddTarget: (String, String, String, Int) -> Unit,
    onIncrementUnit: (Long) -> Unit,
    onDeleteTarget: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var targetTitle by remember { mutableStateOf("") }
    var targetDeadline by remember { mutableStateOf("2026-11-15") }
    var targetPillar by remember { mutableStateOf("Academic") }
    var targetUnits by remember { mutableStateOf("20") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("deadlines_screen_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // Add Target & Deadline Engine Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_target_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "ADD TARGET & DEADLINE ENGINE",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    OutlinedTextField(
                        value = targetTitle,
                        onValueChange = { targetTitle = it },
                        placeholder = { Text("Target Name (e.g. Rotational Dynamics / Q4 Launch)", fontSize = 11.sp, color = TextMuted) },
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
                        OutlinedTextField(
                            value = targetDeadline,
                            onValueChange = { targetDeadline = it },
                            placeholder = { Text("YYYY-MM-DD", fontSize = 11.sp, color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
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

                        // Pillar Selector
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D0F17))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    targetPillar = when (targetPillar) {
                                        "Academic" -> "Financial"
                                        "Financial" -> "Personal"
                                        else -> "Academic"
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = targetPillar,
                                color = when (targetPillar) {
                                    "Academic" -> EmeraldLight
                                    "Financial" -> AmberAccent
                                    else -> IndigoLight
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = targetUnits,
                            onValueChange = { targetUnits = it },
                            placeholder = { Text("Total Units (e.g. 20)", fontSize = 11.sp, color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
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
                                if (targetTitle.isNotBlank()) {
                                    val u = targetUnits.toIntOrNull() ?: 10
                                    onAddTarget(targetTitle.trim(), targetDeadline.trim(), targetPillar, u)
                                    targetTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Text("Create Target Plan", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Active Targets Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ACTIVE DEADLINE TARGETS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${state.targets.size} Active",
                    color = EmeraldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Target Items List
        items(state.targets, key = { it.id }) { target ->
            TargetCardItem(
                target = target,
                onIncrement = { onIncrementUnit(target.id) },
                onDelete = { onDeleteTarget(target.id) }
            )
        }
    }
}

@Composable
private fun TargetCardItem(
    target: TargetDeadline,
    onIncrement: () -> Unit,
    onDelete: () -> Unit
) {
    val pct = if (target.totalUnits > 0) ((target.completedUnits.toFloat() / target.totalUnits) * 100).toInt().coerceIn(0, 100) else 0

    // Days calculation
    val daysLeft = remember(target.deadline) {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val d = format.parse(target.deadline)
            if (d != null) {
                val diff = d.time - System.currentTimeMillis()
                (TimeUnit.MILLISECONDS.toDays(diff)).coerceAtLeast(1)
            } else 14
        } catch (e: Exception) {
            14
        }
    }

    val dailyRequired = if (target.recalculatedDaily != null) {
        String.format(Locale.getDefault(), "%.1f", target.recalculatedDaily)
    } else {
        val rem = (target.totalUnits - target.completedUnits).coerceAtLeast(0)
        String.format(Locale.getDefault(), "%.1f", rem.toDouble() / daysLeft.coerceAtLeast(1))
    }

    val isLagging = pct < 60

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
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(IndigoGlow, RoundedCornerShape(4.dp))
                            .border(1.dp, IndigoAccent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = target.pillar,
                            color = IndigoLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = target.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$daysLeft Days Left",
                        color = if (isLagging) AmberAccent else EmeraldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0D0F17))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(pct / 100f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isLagging) AmberAccent else EmeraldPrimary)
                )
            }

            // Progress details & +1 Done action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress: ${target.completedUnits}/${target.totalUnits} ($pct%)",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Pace: $dailyRequired units/d",
                    color = EmeraldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onIncrement,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("+1 Done", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
