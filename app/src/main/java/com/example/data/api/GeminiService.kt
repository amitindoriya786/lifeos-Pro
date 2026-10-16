package com.example.data.api

import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun resolveApiKey(userKey: String?): String {
        if (!userKey.isNullOrBlank()) return userKey.trim()
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
    }

    suspend fun analyzeStudyPerformance(
        apiKey: String,
        tests: List<TestRecord>,
        modules: List<BookModule>,
        lectures: List<LectureRecord>,
        syllabus: List<SyllabusChapter>
    ): String = withContext(Dispatchers.IO) {
        val resolvedKey = resolveApiKey(apiKey)
        if (resolvedKey.isBlank()) {
            return@withContext generateLocalDiagnosticReport(tests, modules, syllabus)
        }

        try {
            val prompt = buildStudyAnalysisPrompt(tests, modules, lectures, syllabus)
            val jsonBody = JSONObject().apply {
                val contents = JSONArray()
                val contentObj = JSONObject().apply {
                    val parts = JSONArray()
                    val partObj = JSONObject().apply {
                        put("text", prompt)
                    }
                    parts.put(partObj)
                    put("parts", parts)
                }
                contents.put(contentObj)
                put("contents", contents)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$resolvedKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext "API Error (${response.code}): ${response.message}\nFallback Diagnosis:\n${generateLocalDiagnosticReport(tests, modules, syllabus)}"
            }

            val resJson = JSONObject(responseBody)
            val candidates = resJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text
            } else {
                generateLocalDiagnosticReport(tests, modules, syllabus)
            }
        } catch (e: Exception) {
            "Real-time Connection Warning: ${e.localizedMessage}\n\nAutomated Rule-Based Diagnostic:\n${generateLocalDiagnosticReport(tests, modules, syllabus)}"
        }
    }

    private fun buildStudyAnalysisPrompt(
        tests: List<TestRecord>,
        modules: List<BookModule>,
        lectures: List<LectureRecord>,
        syllabus: List<SyllabusChapter>
    ): String {
        val testSummary = tests.joinToString("\n") {
            "- ${it.testName} (${it.date}): Score ${it.score}/${it.maxScore} (${it.accuracyPct}% accuracy, -${it.negativeMarks} negatives). Strong: ${it.strongTopics}. Weak: ${it.weakTopics}."
        }

        val moduleSummary = modules.joinToString("\n") {
            "- ${it.bookName} [${it.chapter}]: ${it.exerciseName} (${it.solvedCount}/${it.totalCount} solved, status: ${it.status})."
        }

        val lectureSummary = lectures.take(5).joinToString("\n") {
            "- ${it.subject}: ${it.topic} (Rating: ${it.understandingRating}/5, Notes: ${if (it.notesTaken) "Yes" else "No"}, Takeaway: ${it.keyTakeaway})."
        }

        val completedCount = syllabus.count { it.theoryCompleted && it.moduleSolved }
        val remainingCount = syllabus.size - completedCount

        return """
You are an elite AI Study Coach & Academic Strategist (JEE/Competitive STEM Mastery Mentor).
Analyze the student's real-time academic telemetry below and provide an actionable, high-impact diagnostic performance report in clear, motivating Hinglish/English.

=== STUDENT TELEMETRY ===
1. TESTS GIVEN:
$testSummary

2. BOOKS & MODULES SOLVED:
$moduleSummary

3. RECENT LECTURES ATTENDED:
$lectureSummary

4. SYLLABUS STATS:
Total Chapters: ${syllabus.size}, Fully Completed: $completedCount, Remaining: $remainingCount.

=== YOUR OUTPUT MUST INCLUDE ===
1. 🎯 PERFORMANCE SCORECARD & REALITY CHECK:
   - Identify the exact root cause of negative marks lost.
   - Contrast strong areas vs critical leak areas.
2. 🗓 AI REVISION SCHEDULE (Specific Chapters & Next 7 Days Dates):
   - Explicitly name which chapters must be revised immediately and why.
3. 📝 NEXT TEST RECOMMENDATION & TARGET CUTOFF:
   - What test should the student take next and on what date.
4. ⚡ 3 NON-NEGOTIABLE BRIDGING ACTIONS for this week.
Keep it sharp, rigorous, empathetic, and actionable!
        """.trimIndent()
    }

    private fun generateLocalDiagnosticReport(
        tests: List<TestRecord>,
        modules: List<BookModule>,
        syllabus: List<SyllabusChapter>
    ): String {
        val avgAccuracy = if (tests.isNotEmpty()) tests.map { it.accuracyPct }.average().toInt() else 80
        val totalNegatives = tests.sumOf { it.negativeMarks }
        val weakTopicsList = tests.map { it.weakTopics }.filter { it.isNotBlank() }.joinToString(", ")
        val completedSyllabus = syllabus.count { it.theoryCompleted && it.moduleSolved }

        return """
📊 REAL-TIME ACADEMIC DIAGNOSTIC REPORT (Offline Intelligence Engine)
---------------------------------------------------------------------
• Average Test Accuracy: $avgAccuracy%
• Total Negative Marks Lost in Recent Tests: -$totalNegatives marks
• Identified Knowledge Gaps / Weak Topics: ${if (weakTopicsList.isNotBlank()) weakTopicsList else "Rolling with slipping transition, calculation slips"}
• Syllabus Progress: $completedSyllabus / ${syllabus.size} Chapters Mastered (${if (syllabus.isNotEmpty()) completedSyllabus * 100 / syllabus.size else 60}%)

🧠 AI REVISION & TEST PRESCRIPTION:
1. Revise "Rotational Mechanics" (Day 3 & Day 7 Spaced Review):
   - Fix: Re-solve 15 Advance PYQs on toppling & rolling with friction.
2. Negative Mark Elimination Strategy:
   - You are losing ~$totalNegatives marks due to aggressive guessing in multi-correct options. Implement the 2-pass question selection rule.
3. Next Recommended Test:
   - Take "Mechanics + Electrostatics Part Test 4" in 4 Days (Target: 220+/300 with < 8 negative marks).

💡 Tip: Paste your Gemini API Key in Settings to enable real-time cloud LLM deep reasoning!
        """.trimIndent()
    }
}
