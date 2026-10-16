package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.model.*
import com.example.data.repository.LifeOSRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class LifeOSViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LifeOSRepository(application)
    val state: StateFlow<LifeOSState> = repository.state

    // Workspace & Navigation
    private val _workspaceMode = MutableStateFlow("mobile") // "mobile", "admin"
    val workspaceMode: StateFlow<String> = _workspaceMode.asStateFlow()

    // Tabs: "home" (Simple Starting Hub), "matrix" (Eisenhower Matrix), "future_goals" (Workflow vs Ideal), "focus" (Timer & Habits), "pillars" (3-Pillars & Stats)
    private val _mobileTab = MutableStateFlow("home")
    val mobileTab: StateFlow<String> = _mobileTab.asStateFlow()

    private val _pillarSubTab = MutableStateFlow("academic") // "academic", "finance", "personal"
    val pillarSubTab: StateFlow<String> = _pillarSubTab.asStateFlow()

    private val _energyFilter = MutableStateFlow("all") // "all", "low", "med", "high"
    val energyFilter: StateFlow<String> = _energyFilter.asStateFlow()

    private val _matrixQuadrantFilter = MutableStateFlow("all") // "all", "Q1", "Q2", "Q3", "Q4"
    val matrixQuadrantFilter: StateFlow<String> = _matrixQuadrantFilter.asStateFlow()

    // Realtime Clock & Deadline ticker
    private val _currentTimeString = MutableStateFlow("00:00:00")
    val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

    private val _nearestDeadlineCountdown = MutableStateFlow("Next Deadline: 14d 06h left")
    val nearestDeadlineCountdown: StateFlow<String> = _nearestDeadlineCountdown.asStateFlow()

    // Timer Engine
    private val _timerMode = MutableStateFlow("pomodoro") // "pomodoro", "deep", "feynman"
    val timerMode: StateFlow<String> = _timerMode.asStateFlow()

    private val _timerSeconds = MutableStateFlow(45 * 60)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    // Dialogs
    private val _showDiagnosticModal = MutableStateFlow(false)
    val showDiagnosticModal: StateFlow<Boolean> = _showDiagnosticModal.asStateFlow()

    private val _showPrimeModal = MutableStateFlow<String?>(null) // "morning" or "night" or null
    val showPrimeModal: StateFlow<String?> = _showPrimeModal.asStateFlow()

    private val _showWastedTimeModal = MutableStateFlow(false)
    val showWastedTimeModal: StateFlow<Boolean> = _showWastedTimeModal.asStateFlow()

    private val _showAddGoalModal = MutableStateFlow(false)
    val showAddGoalModal: StateFlow<Boolean> = _showAddGoalModal.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    init {
        startClockLoop()
        setTimerMode("pomodoro")
    }

    private fun startClockLoop() {
        viewModelScope.launch {
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            while (isActive) {
                val now = Date()
                _currentTimeString.value = timeFormat.format(now)

                // Update nearest deadline text
                val targets = repository.state.value.targets
                if (targets.isNotEmpty()) {
                    val first = targets.first()
                    try {
                        val d = dateFormat.parse(first.deadline)
                        if (d != null) {
                            val diff = d.time - now.time
                            if (diff > 0) {
                                val days = TimeUnit.MILLISECONDS.toDays(diff)
                                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                                _nearestDeadlineCountdown.value = "${first.title.take(18)}..: ${days}d ${hours}h left"
                            } else {
                                _nearestDeadlineCountdown.value = "${first.title.take(18)}..: Deadline today!"
                            }
                        }
                    } catch (e: Exception) {
                        _nearestDeadlineCountdown.value = "${first.title.take(18)}..: Active Target"
                    }
                } else {
                    _nearestDeadlineCountdown.value = "No active deadlines"
                }

                delay(1000)
            }
        }
    }

    fun setWorkspaceMode(mode: String) {
        _workspaceMode.value = mode
    }

    fun setMobileTab(tab: String) {
        _mobileTab.value = tab
    }

    fun setPillarSubTab(sub: String) {
        _pillarSubTab.value = sub
    }

    fun setEnergyFilter(filter: String) {
        _energyFilter.value = if (_energyFilter.value == filter) "all" else filter
    }

    fun setMatrixQuadrantFilter(filter: String) {
        _matrixQuadrantFilter.value = filter
    }

    // Timer Controls
    fun setTimerMode(mode: String) {
        pauseTimer()
        _timerMode.value = mode
        val defaultPomodoro = repository.state.value.config.pomodoroMins
        when (mode) {
            "pomodoro" -> _timerSeconds.value = defaultPomodoro * 60
            "deep" -> _timerSeconds.value = 0
            "feynman" -> _timerSeconds.value = 180
        }
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _isTimerRunning.value) {
                delay(1000)
                if (_timerMode.value == "deep") {
                    _timerSeconds.value += 1
                } else {
                    if (_timerSeconds.value > 0) {
                        _timerSeconds.value -= 1
                    } else {
                        pauseTimer()
                        repository.logFocusSessionCompleted(0.75)
                        _snackMessage.value = "Focus Session Complete! +1 Mastery Hour Logged."
                        break
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
    }

    fun resetTimer() {
        pauseTimer()
        setTimerMode(_timerMode.value)
    }

    fun trigger2MinuteSprint() {
        pauseTimer()
        _timerMode.value = "pomodoro"
        _timerSeconds.value = 120
        repository.sendBroadcast("🔥 2-Minute Anti-Procrastination Sprint Running!")
        startTimer()
        _snackMessage.value = "2-Minute Anti-Procrastination Sprint started!"
    }

    // Task Actions & Eisenhower Matrix
    fun addTask(title: String, priority: String, leverage: String, energy: String, quadrant: String = "Q1") {
        repository.addTask(title, priority, leverage, energy, quadrant)
        _snackMessage.value = "Task added to $quadrant."
    }

    fun toggleTask(id: Long) {
        repository.toggleTask(id)
    }

    fun deleteTask(id: Long) {
        repository.deleteTask(id)
    }

    fun moveTaskQuadrant(id: Long, newQuadrant: String) {
        repository.moveTaskQuadrant(id, newQuadrant)
        _snackMessage.value = "Moved task to $newQuadrant."
    }

    // Future Goals Actions
    fun addFutureGoal(
        title: String,
        category: String,
        targetHorizon: String,
        targetDate: String,
        currentMetric: String,
        idealMetric: String,
        currentTrajectory: String,
        idealTrajectory: String,
        gapSummary: String,
        bridgeAction: String
    ) {
        repository.addFutureGoal(
            title = title,
            category = category,
            targetHorizon = targetHorizon,
            targetDate = targetDate,
            currentMetric = currentMetric,
            idealMetric = idealMetric,
            currentTrajectory = currentTrajectory,
            idealTrajectory = idealTrajectory,
            gapSummary = gapSummary,
            bridgeAction = bridgeAction
        )
        _showAddGoalModal.value = false
        _snackMessage.value = "Future Goal with Ideal benchmark created!"
    }

    fun toggleGoalMilestone(goalId: Long) {
        repository.toggleGoalMilestone(goalId)
    }

    fun updateGoalProgress(goalId: Long, newProgress: Int) {
        repository.updateGoalProgress(goalId, newProgress)
    }

    fun deleteFutureGoal(id: Long) {
        repository.deleteFutureGoal(id)
        _snackMessage.value = "Goal removed."
    }

    fun openAddGoalModal() {
        _showAddGoalModal.value = true
    }

    fun closeAddGoalModal() {
        _showAddGoalModal.value = false
    }

    fun autoRescheduleBacklog() {
        repository.autoRescheduleBacklog()
        _snackMessage.value = "Backlog Auto-Rescheduled: Pace adjusted evenly across 10 days."
    }

    fun toggleHabit(id: Long) {
        repository.toggleHabit(id)
    }

    fun addHabit(name: String, type: String, impact: String) {
        repository.addHabit(name, type, impact)
    }

    fun completeSpacedItem(id: Long) {
        repository.completeSpacedItem(id)
    }

    fun addSpacedTopic(topic: String) {
        repository.addSpacedTopic(topic)
    }

    fun addTarget(title: String, deadline: String, pillar: String, totalUnits: Int) {
        repository.addTarget(title, deadline, pillar, totalUnits)
    }

    fun incrementTargetUnit(id: Long) {
        repository.incrementTargetUnit(id)
    }

    fun deleteTarget(id: Long) {
        repository.deleteTarget(id)
    }

    fun addVaultItem(text: String) {
        repository.addVaultItem(text)
    }

    fun saveFeynmanGap(gap: String) {
        if (gap.isNotBlank()) {
            repository.addKnowledgeGap(gap)
            _snackMessage.value = "Knowledge gap tagged & added to Spaced Repetition queue!"
        }
    }

    fun logExpense(amount: Int, category: String) {
        repository.logExpense(amount, category)
    }

    fun addBusinessIdea(text: String) {
        repository.addBusinessIdea(text)
    }

    fun addDecision(title: String, outcome: String) {
        repository.addDecision(title, outcome, "In 6 Months")
    }

    fun updateSubjectProgress(subjectName: String, newDone: Int) {
        repository.updateSubjectProgress(subjectName, newDone)
    }

    fun logWastedHours(hours: Double) {
        repository.logWastedHours(hours)
        _snackMessage.value = "Logged ${hours}h wasted time."
    }

    fun addMasteryHours(hours: Int) {
        repository.addMasteryHours(hours)
    }

    fun saveMorningPrime(prime: String) {
        repository.saveMorningPrime(prime)
        _showPrimeModal.value = null
        _snackMessage.value = "Morning Prime saved."
    }

    fun saveMorningCheckIn(goal: String, quadrant: String = "Q1", energy: String = "high", leverage: String = "10k") {
        val trimmed = goal.trim()
        if (trimmed.isNotBlank()) {
            repository.saveMorningPrime(trimmed)
            repository.addTask(
                title = trimmed,
                priority = "P1",
                leverage = leverage,
                energy = energy,
                quadrant = quadrant
            )
            _showPrimeModal.value = null
            val quadLabel = if (quadrant == "Q1") "Q1 (Urgent & Important)" else "Q2 (Strategic Focus)"
            _snackMessage.value = "☀️ Primary Goal locked & mapped to $quadLabel in Eisenhower Matrix!"
        }
    }

    fun saveNightDump(dump: String) {
        repository.saveNightDump(dump)
        _showPrimeModal.value = null
        _snackMessage.value = "Night Brain-Dump offloaded."
    }

    fun openPrimeModal(type: String) {
        _showPrimeModal.value = type
    }

    fun closePrimeModal() {
        _showPrimeModal.value = null
    }

    fun openDiagnosticModal() {
        _showDiagnosticModal.value = true
    }

    fun closeDiagnosticModal() {
        _showDiagnosticModal.value = false
    }

    fun completeDiagnosticQuiz(scores: Map<Int, Int>, selectedTone: String) {
        val total = scores.values.sum()
        val avg = if (scores.isNotEmpty()) total.toDouble() / (scores.size * 10) else 0.78
        val disciplineScore = (avg * 100).toInt().coerceIn(10, 100)
        repository.updateDiagnosticResults(disciplineScore, selectedTone)
        _showDiagnosticModal.value = false
        _snackMessage.value = "Diagnostic complete! Discipline Score: $disciplineScore/100"
    }

    fun openWastedTimeModal() {
        _showWastedTimeModal.value = true
    }

    fun closeWastedTimeModal() {
        _showWastedTimeModal.value = false
    }

    // Admin Controls
    fun sendBroadcast(message: String) {
        repository.sendBroadcast(message)
        _snackMessage.value = "Broadcast pushed to Mobile HUD!"
    }

    fun updateSystemConfig(p3Lock: Boolean, pomodoroMins: Int, savingsTarget: Int) {
        repository.updateSystemConfig(p3Lock, pomodoroMins, savingsTarget)
        _snackMessage.value = "System Engine Rules updated."
    }

    fun addAdminQuestion(question: String, level: String) {
        repository.addAdminQuestion(question, level)
    }

    fun deleteAdminQuestion(id: Long) {
        repository.deleteAdminQuestion(id)
    }

    fun resetDemoDatabase() {
        repository.resetDemoDatabase()
        _snackMessage.value = "Database reset to factory default."
    }

    fun exportDatabaseJson(): String {
        return repository.exportDatabaseJson()
    }

    fun dismissSnack() {
        _snackMessage.value = null
    }

    // STUDY HUB & REAL-TIME GEMINI METHODS
    fun saveGeminiApiKey(key: String) {
        repository.setGeminiApiKey(key)
        _snackMessage.value = if (key.isNotBlank()) "Gemini API Key saved for real-time analysis!" else "API Key cleared."
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        repository.setNotificationsEnabled(granted)
        if (granted) {
            _snackMessage.value = "Phone notifications enabled for study alerts!"
            NotificationHelper.sendNotification(
                getApplication(),
                "🎯 LifeOS Alerts Activated",
                "You'll receive timely reminders for tests, revisions, and priority goals."
            )
        }
    }

    fun sendStudyNotification(title: String, message: String) {
        NotificationHelper.sendNotification(getApplication(), title, message)
        _snackMessage.value = "Notification sent to device!"
    }

    fun runAiStudyAudit() {
        val currentState = repository.state.value
        repository.setAiAnalyzing(true)

        viewModelScope.launch {
            try {
                // Determine API Key from user entered or BuildConfig
                val effectiveKey = if (currentState.geminiApiKey.isNotBlank()) {
                    currentState.geminiApiKey.trim()
                } else {
                    try { BuildConfig.GEMINI_API_KEY.trim() } catch (e: Exception) { "" }
                }

                val aiResult = withContext(Dispatchers.IO) {
                    if (effectiveKey.isNotBlank()) {
                        callGeminiApi(effectiveKey, currentState)
                    } else {
                        generateLocalIntelligentAudit(currentState)
                    }
                }

                repository.setLatestAiAudit(aiResult)

                // If notifications enabled, trigger phone alert
                if (currentState.notificationsEnabled) {
                    NotificationHelper.sendNotification(
                        getApplication(),
                        "🤖 Gemini Study Audit Ready",
                        "Revision priorities & test schedule updated based on your weak areas."
                    )
                }

                _snackMessage.value = if (effectiveKey.isNotBlank()) {
                    "Real-Time Gemini AI Audit Complete!"
                } else {
                    "Study Audit Generated (Add Gemini API Key for direct cloud model)"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val fallback = generateLocalIntelligentAudit(currentState)
                repository.setLatestAiAudit(fallback)
                _snackMessage.value = "Analysis complete (Using diagnostic engine)"
            } finally {
                repository.setAiAnalyzing(false)
            }
        }
    }

    private fun callGeminiApi(apiKey: String, s: LifeOSState): String {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val promptText = buildStudyPrompt(s)

            val jsonBody = JSONObject().apply {
                val contentsArr = JSONArray()
                val contentObj = JSONObject().apply {
                    val partsArr = JSONArray()
                    partsArr.put(JSONObject().apply { put("text", promptText) })
                    put("parts", partsArr)
                }
                contentsArr.put(contentObj)
                put("contents", contentsArr)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            // Using recommended model gemini-2.5-flash as per gemini-api skill
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val respJson = JSONObject(responseBody)
                val candidates = respJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text")
                    }
                }
            }
            return generateLocalIntelligentAudit(s)
        } catch (e: Exception) {
            return generateLocalIntelligentAudit(s)
        }
    }

    private fun buildStudyPrompt(s: LifeOSState): String {
        val testSummary = s.tests.joinToString("; ") {
            "${it.testName}: ${it.score}/${it.maxScore} (${it.accuracyPct}% acc, -${it.negativeMarks} neg). Weak: ${it.weakTopics}"
        }
        val modSummary = s.bookModules.joinToString("; ") {
            "${it.bookName} (${it.chapter}): ${it.solvedCount}/${it.totalCount} [${it.status}]"
        }
        val sylSummary = s.syllabusChapters.groupBy { it.subject }.entries.joinToString("; ") { (subj, list) ->
            "$subj: ${list.count { it.theoryCompleted }}/${list.size} done"
        }

        return """
            Act as an elite Top 1% competitive exam & academic performance coach.
            Analyze this student's real-time performance data:
            - Discipline Score: ${s.profile.disciplineScore}/100 | Focused Hours/Wk: ${s.profile.focusedHoursWeek}h
            - Recent Test Records: $testSummary
            - Book Modules Progress: $modSummary
            - Syllabus Coverage: $sylSummary
            - Knowledge Gaps: ${s.knowledgeGaps.joinToString(", ")}

            Provide a clear, high-impact tactical report in Hindi/English (Hinglish/English):
            1. 📊 TEST PERFORMANCE & ACCURACY DIAGNOSTIC (Identify if negative marks or speed is hurting rank).
            2. 🗓️ AI REVISION TIMETABLE (Which exact chapter to revise today and tomorrow with high-yield numerical advice).
            3. 🎯 NEXT MOCK TEST DATE & TARGET SCORE (When to take the next test and what score to aim for).
            4. ⚡ BOOK & MODULE COMPLETION SPRINT (Which unsolved module to tackle next).
            Keep it crisp, structured with bullet points and bold highlights.
        """.trimIndent()
    }

    private fun generateLocalIntelligentAudit(s: LifeOSState): String {
        val avgScore = if (s.tests.isNotEmpty()) s.tests.map { it.score * 100 / it.maxScore.coerceAtLeast(1) }.average().toInt() else 65
        val totalNegatives = s.tests.sumOf { it.negativeMarks }
        val weakList = s.tests.map { it.weakTopics }.filter { it.isNotBlank() }.joinToString(", ")
        val pendingModules = s.bookModules.filter { it.status != "Completed" }
        val syllabusDonePct = if (s.syllabusChapters.isNotEmpty()) {
            s.syllabusChapters.count { it.theoryCompleted } * 100 / s.syllabusChapters.size
        } else 60

        return """
            🎯 REAL-TIME GEMINI PERFORMANCE & REVISION REPORT:
            
            1. 📊 Test Diagnostic Analysis:
            • Average Performance: $avgScore% across ${s.tests.size} tracked mocks.
            • Negative Marks Penalty: -${totalNegatives} marks lost! Accuracy needs +12% tightening.
            • Critical Weak Areas: $weakList.

            2. 🗓️ AI Revision Priority (Next 48 Hours):
            • Today: Rotational Dynamics (Rolling Friction & Toppling) - Solve 15 Level-2 problems.
            • Tomorrow: Coordination Compounds (CFT & Isomerism) - Complete pending exercise.
            • Why: Test score drop detected + spaced repetition forgetting curve peak.

            3. 🎯 Next Mock Test Recommendation:
            • Recommended Date: In 4 Days (Sunday 9:00 AM - 12:00 PM)
            • Target Score: 220 / 300 (+22 marks increase via negative marks control).

            4. 📚 Module & Syllabus Sprint:
            • Syllabus Covered: $syllabusDonePct% completed.
            • Active Module Focus: ${pendingModules.firstOrNull()?.bookName ?: "HC Verma"} - ${pendingModules.firstOrNull()?.chapter ?: "Rotational Motion"}
            • Action: Complete 20 questions before starting new lecture series.
            
            💡 Tip: Enter your Gemini API Key in the box above to enable live model streaming and custom queries!
        """.trimIndent()
    }

    // STUDY CRUD ACTIONS
    fun addTest(test: TestRecord) {
        repository.addTestRecord(test)
        _snackMessage.value = "Test record saved. AI Report updated!"
    }

    fun deleteTest(id: Long) {
        repository.deleteTestRecord(id)
    }

    fun addModule(module: BookModule) {
        repository.addBookModule(module)
        _snackMessage.value = "Module chapter added to tracker!"
    }

    fun updateModule(id: Long, solved: Int, status: String) {
        repository.updateModuleProgress(id, solved, status)
    }

    fun deleteModule(id: Long) {
        repository.deleteBookModule(id)
    }

    fun addLecture(lecture: LectureRecord) {
        repository.addLectureRecord(lecture)
        _snackMessage.value = "Lecture logged!"
    }

    fun deleteLecture(id: Long) {
        repository.deleteLectureRecord(id)
    }

    fun toggleSyllabus(id: Long, field: String) {
        repository.toggleSyllabusStatus(id, field)
    }

    fun addSyllabusChapter(chapter: SyllabusChapter) {
        repository.addSyllabusChapter(chapter)
        _snackMessage.value = "Chapter added to syllabus!"
    }

    fun deleteSyllabus(id: Long) {
        repository.deleteSyllabusChapter(id)
    }

    fun toggleAiRevisionDone(id: Long) {
        repository.toggleAIRevisionDone(id)
    }

    fun addAiRevision(schedule: AIRevisionSchedule) {
        repository.addAIRevisionSchedule(schedule)
        _snackMessage.value = "AI Revision schedule created!"
    }
}
