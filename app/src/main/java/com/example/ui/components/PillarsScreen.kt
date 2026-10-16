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
fun PillarsScreen(
    state: LifeOSState,
    pillarSubTab: String,
    onSubTabChange: (String) -> Unit,
    onAddVaultItem: (String) -> Unit,
    onLogExpense: (Int, String) -> Unit,
    onAddBizIdea: (String) -> Unit,
    onAddDecision: (String, String) -> Unit,
    onUpdateSubjectProgress: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var vaultInput by remember { mutableStateOf("") }
    var expenseAmount by remember { mutableStateOf("") }
    var expenseType by remember { mutableStateOf("impulse") }
    var bizIdeaInput by remember { mutableStateOf("") }
    var decTitle by remember { mutableStateOf("") }
    var decOutcome by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("pillars_screen_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // Sub-Navigation Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LifeOSSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, LifeOSBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "academic" to "1. Academic",
                    "finance" to "2. Business/Fin",
                    "personal" to "3. Personal"
                ).forEach { (sub, label) ->
                    val active = pillarSubTab == sub
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) EmeraldPrimary else Color.Transparent)
                            .clickable { onSubTabChange(sub) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (active) Color.Black else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Comparative Progress vs Target Chart Card
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
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "3-PILLAR TARGET VS ACTUAL (%)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Red = Target Lag Deficit",
                            color = RoseAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Comparison bars
                    val comparisonData = listOf(
                        Triple("Academic & Teaching", 78, 84),
                        Triple("Business & Finance", 69, 75),
                        Triple("Personal Growth", 82, 80)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        comparisonData.forEach { (name, actual, target) ->
                            val lag = actual - target
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = name, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "$actual% (Target: $target%) ${if (lag < 0) "$lag%" else "+$lag%"}",
                                        color = if (lag < 0) RoseAccent else EmeraldLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(0xFF0D0F17))
                                ) {
                                    // Target bar (subtle)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(target / 100f)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFF262A3B))
                                    )
                                    // Actual bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(actual / 100f)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(if (lag < 0) RoseAccent else EmeraldPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sub-Tab Content
        when (pillarSubTab) {
            "academic" -> {
                // 1. Subject & Syllabus Tracker
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
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SUBJECT & SYLLABUS TRACKER",
                                    color = EmeraldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(RoseGlow, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "-6% Lag (Need +25m/day)",
                                        color = RoseAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                state.subjects.forEach { subject ->
                                    val diff = subject.done - subject.expected
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = subject.name,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "${subject.done}% (${if (diff >= 0) "+$diff" else "$diff"}% vs Target)",
                                                    color = if (diff < 0) RoseAccent else EmeraldLight,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                IconButton(
                                                    onClick = { onUpdateSubjectProgress(subject.name, subject.done + 5) },
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.AddCircle,
                                                        contentDescription = "+5%",
                                                        tint = EmeraldPrimary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
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
                                                    .fillMaxWidth(subject.done / 100f)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(if (diff < 0) RoseAccent else EmeraldPrimary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Story, Analogy & Student Doubt Vault
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
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "ANALOGY, HOOK & DOUBT VAULT",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = vaultInput,
                                    onValueChange = { vaultInput = it },
                                    placeholder = { Text("Save teaching analogy or student doubt...", fontSize = 11.sp, color = TextMuted) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AmberAccent,
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
                                        if (vaultInput.isNotBlank()) {
                                            onAddVaultItem(vaultInput.trim())
                                            vaultInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AmberAccent,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Save", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.vault.forEach { v ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(text = "💡 ${v.text}", color = TextPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Lecture Delivery Impact Self-Rating
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
                            Text(
                                text = "LECTURE / SESSION IMPACT LOG",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricBox(label = "Clarity", value = "9.2/10", color = EmeraldLight, modifier = Modifier.weight(1f))
                                MetricBox(label = "Engagement", value = "8.8/10", color = IndigoLight, modifier = Modifier.weight(1f))
                                MetricBox(label = "Knowledge Gaps", value = "${state.knowledgeGaps.size}", color = AmberAccent, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            "finance" -> {
                // Monthly Budget & Safe Spend
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
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MONTHLY BUDGET & BURN RATE",
                                    color = EmeraldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(EmeraldGlow, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Safe Spend: ₹850/day",
                                        color = EmeraldLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricBox(label = "Monthly Target", value = "₹${state.config.savingsTarget}", color = Color.White, modifier = Modifier.weight(1f))
                                MetricBox(label = "Saved So Far", value = "₹${state.finance.saved}", color = EmeraldLight, modifier = Modifier.weight(1f))
                                MetricBox(label = "Impulse Waste", value = "₹${state.finance.impulse}", color = RoseAccent, modifier = Modifier.weight(1f))
                            }

                            // Log Expense row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = expenseAmount,
                                    onValueChange = { expenseAmount = it },
                                    placeholder = { Text("₹ Amount", fontSize = 11.sp, color = TextMuted) },
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

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0D0F17))
                                        .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            expenseType = when (expenseType) {
                                                "impulse" -> "skill"
                                                "skill" -> "need"
                                                else -> "impulse"
                                            }
                                        }
                                        .padding(horizontal = 8.dp, vertical = 14.dp)
                                ) {
                                    Text(
                                        text = when (expenseType) {
                                            "impulse" -> "Impulse / Waste"
                                            "skill" -> "Skill / Business ROI"
                                            else -> "Essential Need"
                                        },
                                        color = when (expenseType) {
                                            "impulse" -> RoseAccent
                                            "skill" -> EmeraldLight
                                            else -> TextSecondary
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        val amt = expenseAmount.toIntOrNull()
                                        if (amt != null && amt > 0) {
                                            onLogExpense(amt, expenseType)
                                            expenseAmount = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Log", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Business Leverage Ratio Card
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "BUSINESS LEVERAGE RATIO", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                Text(text = "68% High-Leverage (₹10k/hr)", color = EmeraldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0D0F17))
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(0.68f)
                                            .background(EmeraldPrimary)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(0.32f)
                                            .background(AmberAccent)
                                    )
                                }
                            }

                            Text(
                                text = "Green: Content, Strategy, Core Teaching • Yellow: Routine Admin",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Business Idea Funnel & Network CRM
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
                                text = "BUSINESS IDEA FUNNEL & NETWORK CRM",
                                color = AmberAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.bizIdeas.forEach { idea ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = idea.text,
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(IndigoGlow, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = idea.stage,
                                                color = IndigoLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = bizIdeaInput,
                                    onValueChange = { bizIdeaInput = it },
                                    placeholder = { Text("New business idea / mentor follow-up...", fontSize = 11.sp, color = TextMuted) },
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
                                        if (bizIdeaInput.isNotBlank()) {
                                            onAddBizIdea(bizIdeaInput.trim())
                                            bizIdeaInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = IndigoAccent,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+ Add", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            "personal" -> {
                // Burnout Shield & Recovery Status
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .border(
                                    width = 3.dp,
                                    color = EmeraldPrimary,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🛡 BURNOUT SHIELD: OPTIMAL",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Peak State",
                                    color = EmeraldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = "Cognitive Load: 72% • Sleep Debt: 1.5 hrs this week",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // CEO Decision Journal
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
                                text = "CEO DECISION JOURNAL",
                                color = IndigoLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )

                            OutlinedTextField(
                                value = decTitle,
                                onValueChange = { decTitle = it },
                                placeholder = { Text("Decision taken (e.g. Launch new batch)", fontSize = 11.sp, color = TextMuted) },
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
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = decOutcome,
                                    onValueChange = { decOutcome = it },
                                    placeholder = { Text("Expected 6-month outcome...", fontSize = 11.sp, color = TextMuted) },
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
                                        if (decTitle.isNotBlank()) {
                                            onAddDecision(decTitle.trim(), decOutcome.trim())
                                            decTitle = ""
                                            decOutcome = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Log", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.decisions.forEach { d ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
                                            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(text = d.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "Expected: ${d.outcome} • Review: ${d.reviewDate}", color = TextSecondary, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Energy Givers vs Drainers Audit
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LifeOSBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "⚡ Energy Givers", color = EmeraldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "• Morning 30m Walk (+38% focus)", color = TextSecondary, fontSize = 9.sp)
                                Text(text = "• Deep Problem Solving", color = TextSecondary, fontSize = 9.sp)
                                Text(text = "• Pre-planned Night Checklist", color = TextSecondary, fontSize = 9.sp)
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "🔻 Energy Drainers", color = RoseAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "• Post-11:30 PM Reels (-45%)", color = TextSecondary, fontSize = 9.sp)
                                Text(text = "• Unplanned Afternoon Calls", color = TextSecondary, fontSize = 9.sp)
                                Text(text = "• Heavy Carb Lunch Slump", color = TextSecondary, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0D0F17), RoundedCornerShape(8.dp))
            .border(1.dp, LifeOSBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 8.sp)
            Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
