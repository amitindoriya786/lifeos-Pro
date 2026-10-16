package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.FutureGoal
import com.example.data.model.LifeOSState
import com.example.ui.theme.*

@Composable
fun FutureGoalsScreen(
    state: LifeOSState,
    onOpenAddGoal: () -> Unit,
    onToggleMilestone: (Long) -> Unit,
    onUpdateProgress: (Long, Int) -> Unit,
    onDeleteGoal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("all") }

    val filteredGoals = remember(state.futureGoals, selectedCategory) {
        if (selectedCategory == "all") state.futureGoals
        else state.futureGoals.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("future_goals_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
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
                        Column {
                            Text(
                                text = "FUTURE GOALS ENGINE",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Your Current Workflow vs Top 1% Ideal Benchmark",
                                color = IndigoLight,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = onOpenAddGoal,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IndigoAccent,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Goal", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Category Filter Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0D0F17), RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "all" to "All Horizons",
                            "Academic" to "Academic",
                            "Business" to "Business",
                            "Personal" to "Personal"
                        ).forEach { (cat, label) ->
                            val active = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) IndigoAccent else Color.Transparent)
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (active) Color.White else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Goals List with Side-by-Side Workflow vs Ideal
        items(filteredGoals, key = { it.id }) { goal ->
            FutureGoalComparisonCard(
                goal = goal,
                onToggleMilestone = { onToggleMilestone(goal.id) },
                onUpdateProgress = { onUpdateProgress(goal.id, it) },
                onDeleteGoal = { onDeleteGoal(goal.id) }
            )
        }
    }
}

@Composable
private fun FutureGoalComparisonCard(
    goal: FutureGoal,
    onToggleMilestone: () -> Unit,
    onUpdateProgress: (Int) -> Unit,
    onDeleteGoal: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Category, Horizon & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(IndigoGlow, RoundedCornerShape(4.dp))
                                .border(1.dp, IndigoAccent.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${goal.category} • ${goal.targetHorizon}",
                                color = IndigoLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Target: ${goal.targetDate}",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }

                    Text(
                        text = goal.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteGoal,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete Goal",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // SIDE-BY-SIDE COMPARISON: YOUR WORKFLOW vs IDEAL TOP 1% BENCHMARK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Column 1: YOUR WORKFLOW
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "YOUR WORKFLOW", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                            Text(text = "${goal.currentProgress}%", color = EmeraldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1E2433))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(goal.currentProgress / 100f)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(EmeraldPrimary)
                            )
                        }

                        Text(
                            text = goal.currentMetric,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = "Trajectory: ${goal.currentTrajectoryDesc}",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            lineHeight = 11.sp
                        )
                    }
                }

                // Column 2: IDEAL BENCHMARK (TOP 1%)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F17)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "IDEAL (TOP 1%)", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                            Text(text = "${goal.idealProgress}%", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1E2433))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(goal.idealProgress / 100f)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(AmberAccent)
                            )
                        }

                        Text(
                            text = goal.idealMetric,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = "Trajectory: ${goal.idealTrajectoryDesc}",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            lineHeight = 11.sp
                        )
                    }
                }
            }

            // Gap Analysis Callout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RoseGlow, RoundedCornerShape(8.dp))
                    .border(1.dp, RoseAccent.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = RoseAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Column {
                        Text(
                            text = "GAP ANALYSIS & REALITY CHECK",
                            color = RoseAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = goal.gapSummary,
                            color = RoseLight,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            // Bridge The Gap Action
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EmeraldGlow, RoundedCornerShape(8.dp))
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Column {
                        Text(
                            text = "ACTION TO BRIDGE THE GAP",
                            color = EmeraldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = goal.bridgeTheGapAction,
                            color = Color.White,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            // Milestone Roadmap
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Milestones: ${goal.completedMilestonesCount}/${goal.keyMilestones.size} Completed",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = onToggleMilestone,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IndigoGlow,
                            contentColor = IndigoLight
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("+ Next Step", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                goal.keyMilestones.forEachIndexed { index, milestone ->
                    val isCompleted = index < goal.completedMilestonesCount
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) EmeraldPrimary else LifeOSBorder)
                        )
                        Text(
                            text = milestone,
                            color = if (isCompleted) TextPrimary else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
