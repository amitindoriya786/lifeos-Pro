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
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun StudyHubScreen(
    state: LifeOSState,
    onRunAiAudit: () -> Unit,
    onSaveApiKey: (String) -> Unit,
    onSendNotification: () -> Unit,
    onAddTest: (TestRecord) -> Unit,
    onDeleteTest: (Long) -> Unit,
    onAddModule: (BookModule) -> Unit,
    onUpdateModule: (Long, Int, String) -> Unit,
    onDeleteModule: (Long) -> Unit,
    onAddLecture: (LectureRecord) -> Unit,
    onDeleteLecture: (Long) -> Unit,
    onToggleSyllabus: (Long, String) -> Unit,
    onAddSyllabusChapter: (SyllabusChapter) -> Unit,
    onDeleteSyllabus: (Long) -> Unit,
    onToggleAiRevisionDone: (Long) -> Unit,
    onAddAiRevision: (AIRevisionSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubColumn by remember { mutableStateOf("tests") } // "tests", "modules", "lectures", "syllabus", "ai_revision"
    var showApiKeyInput by remember { mutableStateOf(false) }
    var apiKeyText by remember(state.geminiApiKey) { mutableStateOf(state.geminiApiKey) }

    // Dialog state
    var showAddTestDialog by remember { mutableStateOf(false) }
    var showAddModuleDialog by remember { mutableStateOf(false) }
    var showAddLectureDialog by remember { mutableStateOf(false) }
    var showAddSyllabusDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("study_hub_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
    ) {
        // Real-Time Gemini AI Top Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.4f)),
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
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = IndigoLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "REAL-TIME GEMINI STUDY AUDITOR",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { showApiKeyInput = !showApiKeyInput },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "API Key",
                                    tint = if (state.geminiApiKey.isNotBlank()) EmeraldPrimary else AmberAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = onSendNotification,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Notification",
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // API Key Config input (collapsible)
                    if (showApiKeyInput) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Gemini API Key (Custom / Real-time):",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedTextField(
                                    value = apiKeyText,
                                    onValueChange = { apiKeyText = it },
                                    placeholder = { Text("Paste your Gemini API Key...", fontSize = 10.sp, color = TextMuted) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = IndigoAccent,
                                        unfocusedBorderColor = LifeOSBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
                                )

                                Button(
                                    onClick = {
                                        onSaveApiKey(apiKeyText)
                                        showApiKeyInput = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Save", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Audit trigger button
                    Button(
                        onClick = onRunAiAudit,
                        enabled = !state.isAiAnalyzing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IndigoAccent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isAiAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gemini AI Analyzing Tests & Modules...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Real-Time Gemini AI Audit & Revision Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Latest Audit Preview Callout
                    if (state.latestAiAudit.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = state.latestAiAudit,
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 5 Study Sub-Columns Selector Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LifeOSSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, LifeOSBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf(
                    "tests" to "1. Tests",
                    "modules" to "2. Modules",
                    "lectures" to "3. Lectures",
                    "syllabus" to "4. Syllabus",
                    "ai_revision" to "5. AI Revise"
                ).forEach { (col, label) ->
                    val isSelected = activeSubColumn == col
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                            .clickable { activeSubColumn = col }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Visual Chart: Study Modules & Syllabus Completion Rates (Interactive Visual Summary)
        item {
            StudyCompletionChartCard(
                state = state,
                onNavigateStudy = { activeSubColumn = "syllabus" }
            )
        }

        // SUB-COLUMN 1: TESTS & DEEP ANALYSIS
        if (activeSubColumn == "tests") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TEST PAPERS & REAL-TIME ANALYSIS",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Button(
                        onClick = { showAddTestDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+ Log Test", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(state.tests, key = { it.id }) { test ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = test.testName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Date: ${test.date}", color = TextMuted, fontSize = 9.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(EmeraldGlow, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "${test.score}/${test.maxScore} (${test.score * 100 / test.maxScore}%)", color = EmeraldLight, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                                IconButton(onClick = { onDeleteTest(test.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // Score breakdown row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Accuracy: ${test.accuracyPct}%", color = IndigoLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Negative Marks: -${test.negativeMarks}", color = RoseAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "P: ${test.physicsScore} | C: ${test.chemistryScore} | M: ${test.mathsScore}", color = TextSecondary, fontSize = 10.sp)
                        }

                        // Strong & Weak tags
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(text = "🟢 Strong: ${test.strongTopics}", color = EmeraldLight, fontSize = 9.sp)
                            Text(text = "🔴 Weak Points: ${test.weakTopics}", color = RoseAccent, fontSize = 9.sp)
                            if (test.aiReport.isNotBlank()) {
                                Text(text = "🤖 AI Verdict: ${test.aiReport}", color = IndigoLight, fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        // SUB-COLUMN 2: MODULE & BOOK SOLVING
        if (activeSubColumn == "modules") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MODULES & BOOK SOLVING TRACKER",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Button(
                        onClick = { showAddModuleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+ Add Book/Module", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(state.bookModules, key = { it.id }) { module ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(text = module.bookName, color = IndigoLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = module.chapter, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                                Text(text = module.exerciseName, color = TextSecondary, fontSize = 10.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(if (module.status == "Completed") EmeraldGlow else AmberGlow, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = module.status, color = if (module.status == "Completed") EmeraldLight else AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { onDeleteModule(module.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // Progress Bar
                        val pct = if (module.totalCount > 0) (module.solvedCount.toFloat() / module.totalCount).coerceIn(0f, 1f) else 0f
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
                                    .fillMaxWidth(pct)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (module.status == "Completed") EmeraldPrimary else AmberAccent)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Solved: ${module.solvedCount}/${module.totalCount} questions (${(pct * 100).toInt()}%)", color = TextSecondary, fontSize = 10.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = {
                                        val next = (module.solvedCount + 5).coerceAtMost(module.totalCount)
                                        val st = if (next >= module.totalCount) "Completed" else "In Progress"
                                        onUpdateModule(module.id, next, st)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGlow, contentColor = EmeraldLight),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 1.dp),
                                    modifier = Modifier.height(22.dp)
                                ) {
                                    Text("+5 Qs", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SUB-COLUMN 3: LECTURE TRACKING
        if (activeSubColumn == "lectures") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LECTURE ATTENDANCE & GRASP LOG",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Button(
                        onClick = { showAddLectureDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+ Log Lecture", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(state.lectures, key = { it.id }) { lecture ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "[${lecture.subject}]", color = IndigoLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Lec #${lecture.lecNumber} • ${lecture.durationMins} mins", color = TextMuted, fontSize = 9.sp)
                                }
                                Text(text = lecture.topic, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "★ ${lecture.understandingRating}/5", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                IconButton(onClick = { onDeleteLecture(lecture.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        if (lecture.keyTakeaway.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D0F17), RoundedCornerShape(6.dp))
                                    .padding(6.dp)
                            ) {
                                Text(text = "💡 Takeaway: ${lecture.keyTakeaway}", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // SUB-COLUMN 4: SYLLABUS REPORT
        if (activeSubColumn == "syllabus") {
            val totalCh = state.syllabusChapters.size.coerceAtLeast(1)
            val fullDone = state.syllabusChapters.count { it.theoryCompleted && it.moduleSolved }
            val syllabusPct = (fullDone * 100 / totalCh)

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "TOTAL SYLLABUS REPORT", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            Text(text = "$fullDone / $totalCh Chapters Mastered ($syllabusPct%)", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0D0F17))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(syllabusPct / 100f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldPrimary)
                            )
                        }
                        Text(
                            text = "Remaining: ${totalCh - fullDone} chapters pending full problem completion.",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "CHAPTER BREAKDOWN", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showAddSyllabusDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+ Add Chapter", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(state.syllabusChapters, key = { it.id }) { chapter ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = chapter.subject, color = IndigoLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(text = "• ${chapter.weightage}", color = TextMuted, fontSize = 8.sp)
                            }
                            Text(text = chapter.chapterName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                            // Status tags (Theory, Module, PYQ)
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FilterChip(
                                    selected = chapter.theoryCompleted,
                                    onClick = { onToggleSyllabus(chapter.id, "theory") },
                                    label = { Text("Theory", fontSize = 8.sp) },
                                    modifier = Modifier.height(24.dp)
                                )
                                FilterChip(
                                    selected = chapter.moduleSolved,
                                    onClick = { onToggleSyllabus(chapter.id, "module") },
                                    label = { Text("Module", fontSize = 8.sp) },
                                    modifier = Modifier.height(24.dp)
                                )
                                FilterChip(
                                    selected = chapter.pyqSolved,
                                    onClick = { onToggleSyllabus(chapter.id, "pyq") },
                                    label = { Text("PYQ", fontSize = 8.sp) },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }

                        IconButton(onClick = { onDeleteSyllabus(chapter.id) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // SUB-COLUMN 5: AI REVISION & GEMINI SCHEDULE
        if (activeSubColumn == "ai_revision") {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(16.dp))
                            Text(
                                text = "AI AUTOMATED REVISION & TEST PRESCRIPTION",
                                color = AmberAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "Gemini AI analyzes test leaks, module progress, and spaced repetition curves to dictate exact revision targets:",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            items(state.aiRevisionSchedules, key = { it.id }) { rev ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (rev.completed) EmeraldPrimary.copy(alpha = 0.4f) else LifeOSBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Checkbox(
                            checked = rev.completed,
                            onCheckedChange = { onToggleAiRevisionDone(rev.id) },
                            colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary, checkmarkColor = Color.Black),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(if (rev.urgency == "High Priority") RoseGlow else IndigoGlow, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(text = rev.urgency, color = if (rev.urgency == "High Priority") RoseAccent else IndigoLight, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = "Scheduled: ${rev.scheduledDate}", color = AmberLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            Text(
                                text = rev.chapterName,
                                color = if (rev.completed) TextMuted else Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Trigger: ${rev.triggerReason}",
                                color = TextSecondary,
                                fontSize = 9.sp
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0D0F17), RoundedCornerShape(6.dp))
                                    .padding(6.dp)
                            ) {
                                Text(text = "🎯 AI Prescription: ${rev.recommendedAction}", color = EmeraldLight, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog 1: Add Test
    if (showAddTestDialog) {
        var tName by remember { mutableStateOf("") }
        var tScore by remember { mutableStateOf("180") }
        var tMax by remember { mutableStateOf("300") }
        var tAcc by remember { mutableStateOf("80") }
        var tNeg by remember { mutableStateOf("8") }
        var tStrong by remember { mutableStateOf("Mechanics Kinematics") }
        var tWeak by remember { mutableStateOf("Friction Toppling, Rotation") }

        AlertDialog(
            onDismissRequest = { showAddTestDialog = false },
            title = { Text("Log Test & Performance Parameters", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = tName, onValueChange = { tName = it }, label = { Text("Test Name", fontSize = 10.sp) })
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = tScore, onValueChange = { tScore = it }, label = { Text("Score", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = tMax, onValueChange = { tMax = it }, label = { Text("Max", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = tAcc, onValueChange = { tAcc = it }, label = { Text("Accuracy %", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = tNeg, onValueChange = { tNeg = it }, label = { Text("Negative Marks", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = tStrong, onValueChange = { tStrong = it }, label = { Text("Strong Topics", fontSize = 10.sp) })
                    OutlinedTextField(value = tWeak, onValueChange = { tWeak = it }, label = { Text("Weak Spots / Silly Errors", fontSize = 10.sp) })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (tName.isNotBlank()) {
                        val s = tScore.toIntOrNull() ?: 150
                        val m = tMax.toIntOrNull() ?: 300
                        val a = tAcc.toIntOrNull() ?: 75
                        val n = tNeg.toIntOrNull() ?: 8
                        onAddTest(
                            TestRecord(
                                testName = tName.trim(),
                                date = "Today",
                                score = s,
                                maxScore = m,
                                accuracyPct = a,
                                negativeMarks = n,
                                physicsScore = s / 3,
                                chemistryScore = s / 3,
                                mathsScore = s / 3,
                                strongTopics = tStrong,
                                weakTopics = tWeak,
                                aiReport = "Logged for AI evaluation."
                            )
                        )
                        showAddTestDialog = false
                    }
                }) { Text("Save Test") }
            },
            dismissButton = { TextButton(onClick = { showAddTestDialog = false }) { Text("Cancel") } }
        )
    }

    // Dialog 2: Add Module
    if (showAddModuleDialog) {
        var bName by remember { mutableStateOf("HC Verma") }
        var chName by remember { mutableStateOf("") }
        var exName by remember { mutableStateOf("Exercise 1") }
        var totQ by remember { mutableStateOf("50") }

        AlertDialog(
            onDismissRequest = { showAddModuleDialog = false },
            title = { Text("Track Book / Module Chapter", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = bName, onValueChange = { bName = it }, label = { Text("Book / Coaching Module", fontSize = 10.sp) })
                    OutlinedTextField(value = chName, onValueChange = { chName = it }, label = { Text("Chapter Name", fontSize = 10.sp) })
                    OutlinedTextField(value = exName, onValueChange = { exName = it }, label = { Text("Exercise", fontSize = 10.sp) })
                    OutlinedTextField(value = totQ, onValueChange = { totQ = it }, label = { Text("Total Questions", fontSize = 10.sp) })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (chName.isNotBlank()) {
                        onAddModule(
                            BookModule(
                                bookName = bName,
                                chapter = chName,
                                exerciseName = exName,
                                solvedCount = 0,
                                totalCount = totQ.toIntOrNull() ?: 50,
                                status = "In Progress"
                            )
                        )
                        showAddModuleDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddModuleDialog = false }) { Text("Cancel") } }
        )
    }

    // Dialog 3: Add Lecture
    if (showAddLectureDialog) {
        var sub by remember { mutableStateOf("Physics") }
        var top by remember { mutableStateOf("") }
        var lecN by remember { mutableStateOf("1") }
        var dur by remember { mutableStateOf("90") }
        var rat by remember { mutableStateOf("5") }
        var take by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddLectureDialog = false },
            title = { Text("Log Lecture Details", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = sub, onValueChange = { sub = it }, label = { Text("Subject (Physics/Chem/Maths)", fontSize = 10.sp) })
                    OutlinedTextField(value = top, onValueChange = { top = it }, label = { Text("Lecture Topic", fontSize = 10.sp) })
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = lecN, onValueChange = { lecN = it }, label = { Text("Lec #", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = dur, onValueChange = { dur = it }, label = { Text("Mins", fontSize = 10.sp) }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = rat, onValueChange = { rat = it }, label = { Text("Rating (1-5)", fontSize = 10.sp) })
                    OutlinedTextField(value = take, onValueChange = { take = it }, label = { Text("Key Formula / Takeaway", fontSize = 10.sp) })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (top.isNotBlank()) {
                        onAddLecture(
                            LectureRecord(
                                subject = sub,
                                topic = top,
                                lecNumber = lecN.toIntOrNull() ?: 1,
                                durationMins = dur.toIntOrNull() ?: 90,
                                understandingRating = rat.toIntOrNull() ?: 5,
                                notesTaken = true,
                                keyTakeaway = take
                            )
                        )
                        showAddLectureDialog = false
                    }
                }) { Text("Log Lecture") }
            },
            dismissButton = { TextButton(onClick = { showAddLectureDialog = false }) { Text("Cancel") } }
        )
    }

    // Dialog 4: Add Syllabus Chapter
    if (showAddSyllabusDialog) {
        var sSub by remember { mutableStateOf("Physics") }
        var sCh by remember { mutableStateOf("") }
        var sW by remember { mutableStateOf("High (8-10%)") }

        AlertDialog(
            onDismissRequest = { showAddSyllabusDialog = false },
            title = { Text("Add Chapter to Syllabus", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = sSub, onValueChange = { sSub = it }, label = { Text("Subject", fontSize = 10.sp) })
                    OutlinedTextField(value = sCh, onValueChange = { sCh = it }, label = { Text("Chapter Name", fontSize = 10.sp) })
                    OutlinedTextField(value = sW, onValueChange = { sW = it }, label = { Text("Weightage (High/Med/Low)", fontSize = 10.sp) })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (sCh.isNotBlank()) {
                        onAddSyllabusChapter(
                            SyllabusChapter(
                                subject = sSub,
                                chapterName = sCh,
                                weightage = sW,
                                theoryCompleted = false,
                                moduleSolved = false,
                                pyqSolved = false,
                                revisionCount = 0
                            )
                        )
                        showAddSyllabusDialog = false
                    }
                }) { Text("Add to Syllabus") }
            },
            dismissButton = { TextButton(onClick = { showAddSyllabusDialog = false }) { Text("Cancel") } }
        )
    }
}
