package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun TopModeBar(
    workspaceMode: String,
    onModeChange: (String) -> Unit,
    onOpenDiagnostic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_mode_bar"),
        color = Color(0xFF10121A),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Title & Live Pulse Dot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = alpha))
                )
                Text(
                    text = "LifeOS Pro",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
                Box(
                    modifier = Modifier
                        .background(EmeraldGlow, RoundedCornerShape(4.dp))
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "v2.4",
                        color = EmeraldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mode Buttons & Diagnostic Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Mobile UI Switch
                Button(
                    onClick = { onModeChange("mobile") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (workspaceMode == "mobile") EmeraldPrimary else LifeOSSurfaceElevated,
                        contentColor = if (workspaceMode == "mobile") Color.Black else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("mode_mobile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Mobile UI",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Admin Panel Switch
                Button(
                    onClick = { onModeChange("admin") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (workspaceMode == "admin") EmeraldPrimary else LifeOSSurfaceElevated,
                        contentColor = if (workspaceMode == "admin") Color.Black else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("mode_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Admin Panel",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // 20-Q Test Button
                OutlinedButton(
                    onClick = onOpenDiagnostic,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = IndigoGlow,
                        contentColor = IndigoLight
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(IndigoAccent.copy(alpha = 0.4f))
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("onboarding_test_button")
                ) {
                    Text(text = "20-Q", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TopLiveHUD(
    state: LifeOSState,
    clockTime: String,
    countdownText: String,
    currentEnergyFilter: String,
    onEnergyFilterChange: (String) -> Unit,
    onSprintTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalTasks = state.tasks.size
    val doneTasks = state.tasks.count { it.done }
    val completionPct = if (totalTasks > 0) (doneTasks * 100 / totalTasks) else 0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_live_hud"),
        color = LifeOSSurface.copy(alpha = 0.95f),
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Coach Announcement Banner with "2-Min Start" button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AmberGlow, RoundedCornerShape(8.dp))
                    .border(1.dp, AmberAccent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Coach Alert",
                        tint = AmberAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = state.config.coachBanner,
                        color = AmberLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = onSprintTrigger,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(24.dp)
                        .testTag("two_min_sprint_button")
                ) {
                    Text(
                        text = "2-Min Start",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clock & Score row + Energy filter + Completion ring
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Clock & Nearest Deadline
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = clockTime,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(IndigoGlow, RoundedCornerShape(4.dp))
                                .border(1.dp, IndigoAccent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Score: ${state.profile.disciplineScore}/100",
                                color = IndigoLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Deadline",
                            tint = RoseAccent,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = countdownText,
                            color = RoseLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Energy Filter & Circular Completion Ring
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Energy Filter
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Energy State",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            listOf(
                                "low" to "⚡",
                                "med" to "⚡⚡",
                                "high" to "⚡⚡⚡"
                            ).forEach { (lvl, icon) ->
                                val selected = currentEnergyFilter == lvl
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) EmeraldPrimary else Color.Transparent)
                                        .clickable { onEnergyFilterChange(lvl) }
                                        .padding(horizontal = 6.dp, vertical = 3.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = icon,
                                        color = if (selected) Color.Black else TextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Progress Ring
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF0D0F17), CircleShape)
                            .border(2.dp, EmeraldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$completionPct%",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "DONE",
                                color = TextMuted,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
