package com.example.data.model

data class TaskItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val priority: String, // "P1", "P2", "P3"
    val leverage: String, // "10k", "100"
    val energy: String, // "low", "med", "high"
    val quadrant: String = "Q1", // "Q1", "Q2", "Q3", "Q4"
    val done: Boolean = false
)

data class FutureGoal(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val category: String, // "Academic", "Business", "Personal"
    val targetHorizon: String, // "90 Days", "6 Months", "1 Year"
    val targetDate: String,
    val currentMetric: String,
    val idealMetric: String,
    val currentProgress: Int, // 0 - 100
    val idealProgress: Int, // 0 - 100
    val currentTrajectoryDesc: String,
    val idealTrajectoryDesc: String,
    val gapSummary: String,
    val bridgeTheGapAction: String,
    val keyMilestones: List<String> = emptyList(),
    val completedMilestonesCount: Int = 1
)

data class TargetDeadline(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val deadline: String, // "YYYY-MM-DD"
    val pillar: String, // "Academic", "Financial", "Personal"
    val totalUnits: Int,
    val completedUnits: Int = 0,
    val recalculatedDaily: Double? = null
)

data class SpacedRepetitionItem(
    val id: Long = System.currentTimeMillis(),
    val topic: String,
    val stage: String,
    val due: String
)

data class HabitItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val type: String, // "good", "bad"
    val impact: String,
    val streak: Int = 0,
    val doneToday: Boolean = false
)

data class AcademicSubject(
    val name: String,
    val done: Int,
    val expected: Int
)

data class VaultItem(
    val id: Long = System.currentTimeMillis(),
    val text: String
)

data class BusinessIdea(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val stage: String
)

data class DecisionItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val outcome: String,
    val reviewDate: String
)

data class DiagnosticOption(
    val label: String,
    val points: Int
)

data class DiagnosticQuestion(
    val id: Long,
    val level: String,
    val question: String,
    val options: List<DiagnosticOption>
)

data class UserProfile(
    val disciplineScore: Int = 78,
    val peakSlot: String = "Morning (5 AM - 11 AM)",
    val coachTone: String = "Strict & Direct Reality Check",
    val masteryHours: Int = 1420,
    val wastedHoursWeek: Double = 4.5,
    val focusedHoursWeek: Double = 26.5,
    val morningPrime: String = "Deliver top-tier Physics concepts & scale footwear brand to Gen-Z.",
    val nightDump: String = "Clear backlog on Electrostatics sheet & review packaging vendor quotes."
)

data class FinanceData(
    val saved: Int = 31200,
    val impulse: Int = 2450,
    val skillInvest: Int = 4200
)

data class SystemConfig(
    val p3LockEnabled: Boolean = true,
    val pomodoroMins: Int = 45,
    val savingsTarget: Int = 45000,
    val coachBanner: String = "Coach: Complete 1 P1 session to unlock P3 tasks!"
)

// === STUDY HUB MODELS ===

data class TestRecord(
    val id: Long = System.currentTimeMillis(),
    val testName: String,
    val date: String,
    val score: Int,
    val maxScore: Int,
    val accuracyPct: Int,
    val negativeMarks: Int,
    val physicsScore: Int,
    val chemistryScore: Int,
    val mathsScore: Int,
    val strongTopics: String,
    val weakTopics: String,
    val aiReport: String = ""
)

data class BookModule(
    val id: Long = System.currentTimeMillis(),
    val bookName: String, // e.g., "HC Verma", "Irodov", "Allen Module", "PYQ 46 Yrs"
    val chapter: String,
    val exerciseName: String,
    val solvedCount: Int,
    val totalCount: Int,
    val status: String = "In Progress" // "In Progress", "Completed", "Needs 2nd Attempt"
)

data class LectureRecord(
    val id: Long = System.currentTimeMillis(),
    val subject: String, // "Physics", "Chemistry", "Maths"
    val topic: String,
    val lecNumber: Int,
    val durationMins: Int,
    val understandingRating: Int, // 1 to 5
    val notesTaken: Boolean,
    val keyTakeaway: String
)

data class SyllabusChapter(
    val id: Long = System.currentTimeMillis(),
    val subject: String,
    val chapterName: String,
    val weightage: String, // "High (8-10%)", "Medium (4-6%)", "Low (2-4%)"
    val theoryCompleted: Boolean = false,
    val moduleSolved: Boolean = false,
    val pyqSolved: Boolean = false,
    val revisionCount: Int = 0
)

data class AIRevisionSchedule(
    val id: Long = System.currentTimeMillis(),
    val chapterName: String,
    val subject: String,
    val scheduledDate: String,
    val triggerReason: String, // e.g., "Test 3 score drop (-12 marks in toppling) + Spaced Day 7"
    val recommendedAction: String,
    val urgency: String = "High Priority", // "High Priority", "Routine", "Pre-Test Polish"
    val completed: Boolean = false
)

data class LifeOSState(
    val config: SystemConfig = SystemConfig(),
    val profile: UserProfile = UserProfile(),
    val tasks: List<TaskItem> = emptyList(),
    val futureGoals: List<FutureGoal> = emptyList(),
    val targets: List<TargetDeadline> = emptyList(),
    val spacedQueue: List<SpacedRepetitionItem> = emptyList(),
    val habits: List<HabitItem> = emptyList(),
    val subjects: List<AcademicSubject> = emptyList(),
    val vault: List<VaultItem> = emptyList(),
    val knowledgeGaps: List<String> = emptyList(),
    val finance: FinanceData = FinanceData(),
    val bizIdeas: List<BusinessIdea> = emptyList(),
    val decisions: List<DecisionItem> = emptyList(),
    val questions: List<DiagnosticQuestion> = emptyList(),

    // Real-time Gemini & Study Ecosystem
    val geminiApiKey: String = "",
    val tests: List<TestRecord> = emptyList(),
    val bookModules: List<BookModule> = emptyList(),
    val lectures: List<LectureRecord> = emptyList(),
    val syllabusChapters: List<SyllabusChapter> = emptyList(),
    val aiRevisionSchedules: List<AIRevisionSchedule> = emptyList(),
    val latestAiAudit: String = "",
    val isAiAnalyzing: Boolean = false,
    val notificationsEnabled: Boolean = false
)
