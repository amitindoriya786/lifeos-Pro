package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DiagnosticQuestion
import com.example.ui.theme.*

@Composable
fun DiagnosticQuizDialog(
    questions: List<DiagnosticQuestion>,
    onDismiss: () -> Unit,
    onComplete: (Map<Int, Int>, String) -> Unit
) {
    if (questions.isEmpty()) return

    var currentIndex by remember { mutableStateOf(0) }
    val scores = remember { mutableStateMapOf<Int, Int>() }
    var chosenCoachTone by remember { mutableStateOf("Strict & Direct Reality Check") }

    val currentQ = questions[currentIndex.coerceIn(0, questions.size - 1)]
    val progress = (currentIndex + 1).toFloat() / questions.size.toFloat()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeOSSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("diagnostic_quiz_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DIAGNOSTIC ASSESSMENT (${currentIndex + 1}/${questions.size})",
                        color = IndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF0D0F17))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(2.dp))
                            .background(IndigoAccent)
                    )
                }

                // Level Badge
                Box(
                    modifier = Modifier
                        .background(IndigoGlow, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Level: ${currentQ.level}",
                        color = IndigoLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Question Text
                Text(
                    text = currentQ.question,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )

                // Options List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentQ.options.forEach { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0D0F17))
                                .border(1.dp, LifeOSBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    scores[currentIndex] = opt.points
                                    if (currentIndex == 19) {
                                        chosenCoachTone = opt.label
                                    }
                                    if (currentIndex < questions.size - 1) {
                                        currentIndex++
                                    } else {
                                        onComplete(scores.toMap(), chosenCoachTone)
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt.label,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(IndigoGlow, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${opt.points} pts",
                                    color = IndigoLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom Nav
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) currentIndex--
                        },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Previous", fontSize = 10.sp)
                    }

                    Button(
                        onClick = {
                            onComplete(scores.toMap(), chosenCoachTone)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGlow,
                            contentColor = EmeraldLight
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Save & Calculate Profile", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
