package com.example.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.LifeOSState
import com.example.data.model.TaskItem
import com.example.ui.theme.*

@Composable
fun EisenhowerMatrixScreen(
    state: LifeOSState,
    selectedQuadrantFilter: String,
    onQuadrantFilterChange: (String) -> Unit,
    onAddTask: (String, String, String, String, String) -> Unit,
    onToggleTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onMoveQuadrant: (Long, String) -> Unit,
    onOpenMorningCheckIn: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedAddQuadrant by remember { mutableStateOf("Q2") } // default to Q2 (the 1% sweet spot)
    var viewMode by remember { mutableStateOf("grid") } // "grid" or "list"

    val q1Tasks = state.tasks.filter { it.quadrant == "Q1" }
    val q2Tasks = state.tasks.filter { it.quadrant == "Q2" }
    val q3Tasks = state.tasks.filter { it.quadrant == "Q3" }
    val q4Tasks = state.tasks.filter { it.quadrant == "Q4" }

    val totalTasks = state.tasks.size.coerceAtLeast(1)
    val q2Ratio = (q2Tasks.size * 100 / totalTasks)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("eisenhower_matrix_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
    ) {
        // Matrix Header & 1% Mastery Sweetspot meter
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
                                text = "EISENHOWER MATRIX",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Urgent vs Important Decision Framework",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        // View mode pill
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewMode == "grid") EmeraldPrimary else Color.Transparent)
                                    .clickable { viewMode = "grid" }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "2x2 Grid",
                                    color = if (viewMode == "grid") Color.Black else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewMode == "list") EmeraldPrimary else Color.Transparent)
                                    .clickable { viewMode = "list" }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "List",
                                    color = if (viewMode == "list") Color.Black else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (viewMode == "html") EmeraldPrimary else Color.Transparent)
                                    .clickable { viewMode = "html" }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🌐 HTML App",
                                    color = if (viewMode == "html") Color.Black else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Q2 Strategic Ratio Bar
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Q2 Top 1% Sweet Spot Ratio", color = TextSecondary, fontSize = 10.sp)
                            Text(text = "$q2Ratio% in Q2 (Strategic)", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
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
                                    .fillMaxWidth(q2Ratio / 100f)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(EmeraldPrimary)
                            )
                        }
                    }
                }
            }
        }

        // Morning Check-In Callout for Top-Level Priority Mapping
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenMorningCheckIn() }
                    .testTag("eisenhower_morning_checkin_banner")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            contentDescription = "Morning Check-In",
                            tint = AmberAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "DAILY MORNING CHECK-IN",
                                color = AmberLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldGlow)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "AUTO-MAP TO Q1",
                                    color = EmeraldLight,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = if (state.profile.morningPrime.isNotBlank())
                                "Today's Focus: ${state.profile.morningPrime}"
                            else
                                "Define today's primary goal to automatically map as Top Priority Task #1.",
                            color = if (state.profile.morningPrime.isNotBlank()) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = onOpenMorningCheckIn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("matrix_morning_checkin_button")
                    ) {
                        Text("Check In", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Add Task into Selected Quadrant
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ADD TASK TO QUADRANT",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Quadrant Select Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "Q1" to "Q1: Do First",
                            "Q2" to "Q2: Schedule",
                            "Q3" to "Q3: Delegate",
                            "Q4" to "Q4: Eliminate"
                        ).forEach { (q, label) ->
                            val isSelected = selectedAddQuadrant == q
                            val qColor = when (q) {
                                "Q1" -> RoseAccent
                                "Q2" -> EmeraldPrimary
                                "Q3" -> AmberAccent
                                else -> IndigoLight
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) qColor.copy(alpha = 0.2f) else Color(0xFF0D0F17))
                                    .border(1.dp, if (isSelected) qColor else LifeOSBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedAddQuadrant = q }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = q,
                                    color = if (isSelected) qColor else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Input & Submit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            placeholder = {
                                Text(
                                    text = when (selectedAddQuadrant) {
                                        "Q1" -> "Add urgent & critical task..."
                                        "Q2" -> "Add long-term high leverage goal..."
                                        "Q3" -> "Add delegate/routine chore..."
                                        else -> "Add distractive habit to eliminate..."
                                    },
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            },
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
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newTaskTitle.isNotBlank()) {
                                    val prio = if (selectedAddQuadrant == "Q1" || selectedAddQuadrant == "Q2") "P1" else "P2"
                                    val lev = if (selectedAddQuadrant == "Q2") "10k" else "100"
                                    val eng = if (selectedAddQuadrant == "Q1" || selectedAddQuadrant == "Q2") "high" else "med"
                                    onAddTask(newTaskTitle.trim(), prio, lev, eng, selectedAddQuadrant)
                                    newTaskTitle = ""
                                }
                            })
                        )

                        Button(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    val prio = if (selectedAddQuadrant == "Q1" || selectedAddQuadrant == "Q2") "P1" else "P2"
                                    val lev = if (selectedAddQuadrant == "Q2") "10k" else "100"
                                    val eng = if (selectedAddQuadrant == "Q1" || selectedAddQuadrant == "Q2") "high" else "med"
                                    onAddTask(newTaskTitle.trim(), prio, lev, eng, selectedAddQuadrant)
                                    newTaskTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (selectedAddQuadrant) {
                                    "Q1" -> RoseAccent
                                    "Q2" -> EmeraldPrimary
                                    "Q3" -> AmberAccent
                                    else -> IndigoAccent
                                },
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("+ Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // VIEW MODE: 2x2 Interactive Grid vs Single Quadrant List vs Standalone HTML App
        if (viewMode == "html") {
            item {
                HtmlAppPreviewCard()
            }
        } else if (viewMode == "grid") {
            // Row 1: Q1 (Urgent & Important) and Q2 (Not Urgent & Important)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuadrantGridCard(
                        quadrantId = "Q1",
                        title = "Q1: DO FIRST",
                        subtitle = "Urgent & Important",
                        tagline = "Crises & Deadlines",
                        accentColor = RoseAccent,
                        tasks = q1Tasks,
                        onToggleTask = onToggleTask,
                        onDeleteTask = onDeleteTask,
                        onMoveQuadrant = onMoveQuadrant,
                        modifier = Modifier.weight(1f)
                    )

                    QuadrantGridCard(
                        quadrantId = "Q2",
                        title = "Q2: SCHEDULE",
                        subtitle = "Not Urgent • Important",
                        tagline = "₹10k/hr 1% Mastery",
                        accentColor = EmeraldPrimary,
                        tasks = q2Tasks,
                        onToggleTask = onToggleTask,
                        onDeleteTask = onDeleteTask,
                        onMoveQuadrant = onMoveQuadrant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Row 2: Q3 (Urgent & Not Important) and Q4 (Not Urgent & Not Important)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuadrantGridCard(
                        quadrantId = "Q3",
                        title = "Q3: DELEGATE",
                        subtitle = "Urgent • Not Important",
                        tagline = "Interruptions & Chores",
                        accentColor = AmberAccent,
                        tasks = q3Tasks,
                        onToggleTask = onToggleTask,
                        onDeleteTask = onDeleteTask,
                        onMoveQuadrant = onMoveQuadrant,
                        modifier = Modifier.weight(1f)
                    )

                    QuadrantGridCard(
                        quadrantId = "Q4",
                        title = "Q4: ELIMINATE",
                        subtitle = "Not Urgent • Not Important",
                        tagline = "Distractions & Waste",
                        accentColor = IndigoLight,
                        tasks = q4Tasks,
                        onToggleTask = onToggleTask,
                        onDeleteTask = onDeleteTask,
                        onMoveQuadrant = onMoveQuadrant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Focused List View
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D0F17), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("all", "Q1", "Q2", "Q3", "Q4").forEach { q ->
                        val isSelected = selectedQuadrantFilter == q
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                                .clickable { onQuadrantFilterChange(q) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = q.uppercase(),
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            val displayedTasks = if (selectedQuadrantFilter == "all") state.tasks
            else state.tasks.filter { it.quadrant == selectedQuadrantFilter }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    displayedTasks.forEach { task ->
                        DetailedTaskCard(
                            task = task,
                            onToggle = { onToggleTask(task.id) },
                            onDelete = { onDeleteTask(task.id) },
                            onMove = { onMoveQuadrant(task.id, it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuadrantGridCard(
    quadrantId: String,
    title: String,
    subtitle: String,
    tagline: String,
    accentColor: Color,
    tasks: List<TaskItem>,
    onToggleTask: (Long) -> Unit,
    onDeleteTask: (Long) -> Unit,
    onMoveQuadrant: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "${tasks.size}",
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Text(
                text = "$subtitle • $tagline",
                color = TextMuted,
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(color = LifeOSBorder, thickness = 0.8.dp)

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks here",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tasks.forEach { task ->
                        var showMoveMenu by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F17), RoundedCornerShape(6.dp))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Checkbox(
                                    checked = task.done,
                                    onCheckedChange = { onToggleTask(task.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = accentColor,
                                        checkmarkColor = Color.Black,
                                        uncheckedColor = LifeOSBorder
                                    ),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = task.title,
                                    color = if (task.done) TextMuted else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Box {
                                IconButton(
                                    onClick = { showMoveMenu = true },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Move",
                                        tint = TextMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMoveMenu,
                                    onDismissRequest = { showMoveMenu = false },
                                    modifier = Modifier.background(LifeOSSurface)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("→ Q1: Urgent & Important", color = RoseAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            onMoveQuadrant(task.id, "Q1")
                                            showMoveMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("→ Q2: Not Urgent & Important", color = EmeraldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            onMoveQuadrant(task.id, "Q2")
                                            showMoveMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("→ Q3: Urgent & Not Important", color = AmberAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            onMoveQuadrant(task.id, "Q3")
                                            showMoveMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("→ Q4: Not Urgent & Not Important", color = IndigoLight, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                        onClick = {
                                            onMoveQuadrant(task.id, "Q4")
                                            showMoveMenu = false
                                        }
                                    )
                                    HorizontalDivider(color = LifeOSBorder)
                                    DropdownMenuItem(
                                        text = { Text("Delete Task", color = RoseAccent, fontSize = 10.sp) },
                                        onClick = {
                                            onDeleteTask(task.id)
                                            showMoveMenu = false
                                        }
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

@Composable
private fun DetailedTaskCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onMove: (String) -> Unit
) {
    val qColor = when (task.quadrant) {
        "Q1" -> RoseAccent
        "Q2" -> EmeraldPrimary
        "Q3" -> AmberAccent
        else -> IndigoLight
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = task.done,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = qColor,
                        checkmarkColor = Color.Black,
                        uncheckedColor = LifeOSBorder
                    ),
                    modifier = Modifier.size(20.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        color = if (task.done) TextMuted else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(qColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = when (task.quadrant) {
                                    "Q1" -> "Q1: Urgent & Important"
                                    "Q2" -> "Q2: Not Urgent • Important"
                                    "Q3" -> "Q3: Urgent • Not Important"
                                    else -> "Q4: Eliminate"
                                },
                                color = qColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (task.leverage == "10k") {
                            Text(
                                text = "₹10k/hr Leverage",
                                color = EmeraldLight,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                var showDetailedMoveMenu by remember { mutableStateOf(false) }

                Box {
                    IconButton(
                        onClick = { showDetailedMoveMenu = true },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Shift Quadrant",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showDetailedMoveMenu,
                        onDismissRequest = { showDetailedMoveMenu = false },
                        modifier = Modifier.background(LifeOSSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("→ Q1: Urgent & Important", color = RoseAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            onClick = {
                                onMove("Q1")
                                showDetailedMoveMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("→ Q2: Not Urgent & Important", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            onClick = {
                                onMove("Q2")
                                showDetailedMoveMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("→ Q3: Urgent & Not Important", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            onClick = {
                                onMove("Q3")
                                showDetailedMoveMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("→ Q4: Not Urgent & Not Important", color = IndigoLight, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            onClick = {
                                onMove("Q4")
                                showDetailedMoveMenu = false
                            }
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(26.dp)
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
    }
}

@Composable
private fun HtmlAppPreviewCard() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("html_app_preview_section")
    ) {
        // Hero Header Card with Copy & Export buttons
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "STANDALONE HTML5 APP",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Convertible to APK via WebView / Capacitor / Cordova",
                                color = EmeraldLight,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            try {
                                val htmlContent = context.assets.open("eisenhower_matrix.html")
                                    .bufferedReader().use { it.readText() }
                                clipboardManager.setText(AnnotatedString(htmlContent))
                                isCopied = true
                                Toast.makeText(context, "Full HTML code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Copied HTML template!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) EmeraldPrimary else IndigoAccent,
                            contentColor = if (isCopied) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("copy_html_code_button")
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isCopied) "Copied!" else "Copy HTML", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "This standalone HTML/JS Eisenhower Matrix contains drag-and-drop, localStorage persistence, and 4-quadrant categorization (Urgent/Important, Urgent/Not Important, Not Urgent/Important, Not Urgent/Not Important). You can save it as index.html and convert it into a mobile app in minutes.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        // Live Interactive WebView of the HTML
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF07090E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F121C))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LIVE HTML APP PREVIEW (4 QUADRANTS WITH DRAG & DROP)",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "OFFLINE READY",
                        color = EmeraldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.allowFileAccess = true
                            webViewClient = WebViewClient()
                            loadUrl("file:///android_asset/eisenhower_matrix.html")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(480.dp)
                )
            }
        }

        // Guide Card: How to convert HTML to Android App (APK)
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
                Text(
                    text = "📱 HOW TO CONVERT THIS HTML TO AN ANDROID APP (APK)",
                    color = AmberLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "Method 1: Capacitor / Cordova (Recommended & Fastest)\n" +
                           "1. Run: npm init @capacitor/app my-matrix-app\n" +
                           "2. Paste this copied HTML code into www/index.html\n" +
                           "3. Run: npx cap add android && npx cap open android\n" +
                           "4. Click 'Build APK' in Android Studio. Done!\n\n" +
                           "Method 2: Android Studio WebView Wrapper\n" +
                           "1. Create an Empty Android project in Android Studio\n" +
                           "2. Put index.html in app/src/main/assets/\n" +
                           "3. In MainActivity, add: webView.loadUrl(\"file:///android_asset/index.html\")\n\n" +
                           "Method 3: Zero-Code PWA / Web2APK\n" +
                           "Upload index.html to Netlify/GitHub or use PWABuilder.com to download a signed APK directly.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
