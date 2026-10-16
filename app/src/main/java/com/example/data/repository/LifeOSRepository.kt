package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class LifeOSRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lifeos_pro_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(loadInitialState())
    val state: StateFlow<LifeOSState> = _state.asStateFlow()

    init {
        saveStateToDisk(_state.value)
    }

    private fun loadInitialState(): LifeOSState {
        val jsonString = prefs.getString("db_snapshot", null)
        if (!jsonString.isNullOrEmpty()) {
            try {
                return parseJsonToState(jsonString)
            } catch (e: Exception) {
                // fallback to default
            }
        }
        return createDefaultState()
    }

    private fun createDefaultState(): LifeOSState {
        val defaultQuestions = listOf(
            DiagnosticQuestion(1, "Basic", "1. Wake-up & Sleep Window: Kya aapki sone aur uthne ki timing roz fix rehti hai?",
                listOf(DiagnosticOption("Haan, roz +/- 30 min ke andar fix hai", 10), DiagnosticOption("Weekdays par fix, weekends par bigadti hai", 6), DiagnosticOption("Bilkull irregular hai", 2))),
            DiagnosticQuestion(2, "Basic", "2. Peak Energy Slot: Din ke kis waqt aapka dimag sabse tez chalta hai?",
                listOf(DiagnosticOption("Early Morning (5 AM - 11 AM)", 10), DiagnosticOption("Afternoon / Evening (12 PM - 8 PM)", 8), DiagnosticOption("Late Night Owl (10 PM - 3 AM)", 7))),
            DiagnosticQuestion(3, "Basic", "3. Screen Time Reality: Din mein bina kaam ke (Reels/Shorts) kitna phone chalta hai?",
                listOf(DiagnosticOption("Less than 45 mins/day", 10), DiagnosticOption("1 se 2.5 ghante roz", 5), DiagnosticOption("3+ ghante nikal jate hain", 1))),
            DiagnosticQuestion(4, "Basic", "4. Planning Habit: Kya aap apna din shuru hone se pehle tasks likhte hain?",
                listOf(DiagnosticOption("Raat ko hi agle din ke P1 tasks likh leta hoon", 10), DiagnosticOption("Subah uth kar sochta hoon", 6), DiagnosticOption("Bina likhe mood ke hisab se chalta hoon", 2))),
            DiagnosticQuestion(5, "Basic", "5. Physical Energy: Hafte mein kitne din workout/walk karte hain?",
                listOf(DiagnosticOption("5-6 din + clean diet", 10), DiagnosticOption("2-3 din walk ya light activity", 6), DiagnosticOption("Zero physical activity + frequent junk food", 2))),
            DiagnosticQuestion(6, "Basic", "6. Financial Tracking: Kya aapko mahine ke end mein har kharche ka hisab pata hota hai?",
                listOf(DiagnosticOption("100% tracked with budget & savings cap", 10), DiagnosticOption("Mota-mota andaza rehta hai", 5), DiagnosticOption("Pata hi nahi chalta paisa kahan gaya", 2))),
            DiagnosticQuestion(7, "Basic", "7. Deep Sitting Capacity: Bina phone chhue lagatar kitni der deep focus kar sakte hain?",
                listOf(DiagnosticOption("60+ mins effortlessly", 10), DiagnosticOption("30 se 45 mins", 7), DiagnosticOption("15-20 min mein dhyan bhatak jata hai", 3))),
            DiagnosticQuestion(8, "Intermediate", "8. Deadline Reaction: Bade target ki deadline aane par aap kaise kaam karte hain?",
                listOf(DiagnosticOption("Daily micro-targets se pehle hi finish karta hoon", 10), DiagnosticOption("Thoda backlog hota hai par cover ho jata hai", 6), DiagnosticOption("Last 20% time mein panic-work karta hoon", 2))),
            DiagnosticQuestion(9, "Intermediate", "9. Task Friction: Jab koi topic ya kaam mushkil lagta hai toh pehla reaction kya hota hai?",
                listOf(DiagnosticOption("Use chote steps mein tod kar push karta hoon", 10), DiagnosticOption("Dusra aasan kaam pakad leta hoon", 5), DiagnosticOption("Turant break le kar phone chalane lagta hoon", 2))),
            DiagnosticQuestion(10, "Intermediate", "10. Primary Distraction Trigger: Focus todne mein sabse bada role kiska hai?",
                listOf(DiagnosticOption("Rarely distracted (Phone silent rehta hai)", 10), DiagnosticOption("Overthinking & daydreaming", 5), DiagnosticOption("Social media notifications & Reels", 2))),
            DiagnosticQuestion(11, "Intermediate", "11. Habit Consistency: Jab aap koi nayi habit shuru karte hain toh kitne din tikti hai?",
                listOf(DiagnosticOption("21+ days tak consistent rehta hoon", 10), DiagnosticOption("10-14 din baad streak tut jati hai", 6), DiagnosticOption("3-5 din mein josh khatam ho jata hai", 2))),
            DiagnosticQuestion(12, "Intermediate", "12. Unplanned Free Time: Achanak 2 ghante khali milein toh aap kya karte hain?",
                listOf(DiagnosticOption("High-leverage skill, reading ya backlog clear", 10), DiagnosticOption("Rest ya dosto se baat", 6), DiagnosticOption("Random scrolling & binge watching", 2))),
            DiagnosticQuestion(13, "Intermediate", "13. Stress/Impulse Spending: Bored ya stressed hone par online order/shopping karte hain?",
                listOf(DiagnosticOption("Kabhi nahi, 24-hr rule follow karta hoon", 10), DiagnosticOption("Mahine mein 1-2 baar food/snacks", 6), DiagnosticOption("Aksar bina zaroorat kharch ho jata hai", 2))),
            DiagnosticQuestion(14, "Intermediate", "14. Evening Guilt: Hafte mein kitni raat lagta hai ki aaj ka din barbad ho gaya?",
                listOf(DiagnosticOption("0-1 raat (Zyadatar din productive hote hain)", 10), DiagnosticOption("2-3 raat", 5), DiagnosticOption("Lagbhag har roz guilt hota hai", 2))),
            DiagnosticQuestion(15, "Advanced", "15. Bad Day Recovery: Agar ek din pura kharab ho jaye toh agle din kya karte hain?",
                listOf(DiagnosticOption("Bina guilt ke auto-reschedule karke fresh start", 10), DiagnosticOption("Agle din double bojh le leta hoon", 6), DiagnosticOption("Guilt mein 2-3 din aur kharab ho jate hain", 2))),
            DiagnosticQuestion(16, "Advanced", "16. Leverage Clarity: 5 kaam hon aur waqt sirf 2 ka, toh pehle kya chunte hain?",
                listOf(DiagnosticOption("Jo long-term ₹10,000/hr impact de (P1)", 10), DiagnosticOption("Jiski deadline sabse paas ho", 7), DiagnosticOption("Jo sabse aasan aur jaldi tick ho jaye", 3))),
            DiagnosticQuestion(17, "Advanced", "17. Feynman Mastery: Koi concept padhne ke baad kya bina dekhe samjha sakte hain?",
                listOf(DiagnosticOption("Haan, real-life analogy ke sath sikha sakta hoon", 10), DiagnosticOption("Thoda notes dekhna padta hai", 6), DiagnosticOption("Padh leta hoon par bolte waqt atakta hoon", 3))),
            DiagnosticQuestion(18, "Advanced", "18. Self-Accountability: Target miss hone par pehla thought kya aata hai?",
                listOf(DiagnosticOption("Meri planning/execution mein kahan kami rahi?", 10), DiagnosticOption("Kaam hi bahut zyada tha", 5), DiagnosticOption("Bahari halat ya dusre logon ki galti thi", 2))),
            DiagnosticQuestion(19, "Advanced", "19. 90-Day North Star: Agle 90 din mein aapka sabse bada focus pillar konsa hai?",
                listOf(DiagnosticOption("Academic & Teaching Mastery (Top 1% Educator)", 10), DiagnosticOption("Business Scaling & Financial Growth", 10), DiagnosticOption("Personal Discipline, Health & Mental Peak", 10))),
            DiagnosticQuestion(20, "Advanced", "20. AI Notification Coaching Style: Kaam taalne par app aapko kis tone mein alert kare?",
                listOf(DiagnosticOption("Strict & Direct Reality Check", 10), DiagnosticOption("Logical Data & Lag Cost Analyst", 9), DiagnosticOption("Calm & Supportive Coach", 8)))
        )

        val defaultFutureGoals = listOf(
            FutureGoal(
                id = 1001,
                title = "Top 1% Physics Educator & National Authority",
                category = "Academic",
                targetHorizon = "90 Days",
                targetDate = "2026-12-31",
                currentMetric = "2.5h / day content creation (Pace: 1 Module/wk)",
                idealMetric = "4.0h / day uninterrupted deep focus (Pace: 2 Modules/wk)",
                currentProgress = 62,
                idealProgress = 85,
                currentTrajectoryDesc = "Estimated completion in 14 months at current interruption rate",
                idealTrajectoryDesc = "Top 1% Benchmark reaches full course completion in 6.5 months",
                gapSummary = "1.5h lost daily to unscheduled student doubts & WhatsApp replies",
                bridgeTheGapAction = "Designate 4:30 PM - 5:15 PM as sole doubt clearing window. Protect 6:00 AM - 9:30 AM as sacred zero-phone creation slot.",
                keyMilestones = listOf(
                    "Mechanics & Electrostatics Master Series (Recorded)",
                    "1,000 High-Yield Scaffolded Questions Vault",
                    "Interactive Visual Problem Simulator Launch",
                    "All-India Mock Test Series Linkage"
                ),
                completedMilestonesCount = 2
            ),
            FutureGoal(
                id = 1002,
                title = "Scale Handcrafted Footwear Brand to ₹10L/mo",
                category = "Business",
                targetHorizon = "6 Months",
                targetDate = "2027-03-31",
                currentMetric = "₹1.4L / month (Conversion Rate: 1.8%, 3 Reels/wk)",
                idealMetric = "₹10.0L / month (Conversion Rate: 3.4%, 7 Reels/wk + Creator Collabs)",
                currentProgress = 42,
                idealProgress = 78,
                currentTrajectoryDesc = "Gradual organic growth reaching ₹3.5L/mo in 6 months",
                idealTrajectoryDesc = "Ideal Top 1% Scale reaches ₹10L/mo run rate via Creator affiliate leverage",
                gapSummary = "Lack of weekly short-form video batching & delay in premium packaging contract",
                bridgeTheGapAction = "Batch shoot 7 reels every Sunday morning. Finalize packaging vendor contract this Tuesday.",
                keyMilestones = listOf(
                    "Festive Batch Production (100 Pairs Ready)",
                    "Creator Seeding Campaign (15 Micro-Influencers)",
                    "Website Conversion Rate Optimization (CRO > 3%)",
                    "Exclusive Festive Giftbox Packaging Launch"
                ),
                completedMilestonesCount = 1
            ),
            FutureGoal(
                id = 1003,
                title = "Single-Digit Body Fat & Peak Cognitive State",
                category = "Personal",
                targetHorizon = "1 Year",
                targetDate = "2027-10-15",
                currentMetric = "3.5 workouts/wk, 6.2h sleep (Cognitive Focus: 72%)",
                idealMetric = "6 workouts/wk, 7.5h clean sleep (Cognitive Focus: 94%)",
                currentProgress = 70,
                idealProgress = 90,
                currentTrajectoryDesc = "Moderate energy levels with recurring 2:00-4:00 PM cognitive slumps",
                idealTrajectoryDesc = "Zero brain fog, sustained 4-hour morning deep focus sessions",
                gapSummary = "Post-11:30 PM screen time & irregular dinners delaying deep restorative REM sleep",
                bridgeTheGapAction = "Hard phone cutoff at 11:00 PM. 20-minute morning outdoor walk within 30 mins of waking.",
                keyMilestones = listOf(
                    "Consistent 6:00 AM Wakeup Streak (21 Days)",
                    "Sub-15% Body Fat Benchmark Reached",
                    "90-Min Deep Sitting Focus Without Fidgeting",
                    "Zero Afternoon Carb Crash Diet Routine"
                ),
                completedMilestonesCount = 2
            )
        )

        // Study Hub Defaults
        val defaultTests = listOf(
            TestRecord(
                id = 2001,
                testName = "JEE Advanced Full Mock 01",
                date = "2026-10-04",
                score = 198,
                maxScore = 300,
                accuracyPct = 78,
                negativeMarks = 16,
                physicsScore = 68,
                chemistryScore = 66,
                mathsScore = 64,
                strongTopics = "Kinematics, Work Energy, Mole Concept, Matrices",
                weakTopics = "Toppling & Friction, Ionic Equilibrium, Coordinate Geometry",
                aiReport = "Strong conceptual foundation in Kinematics; heavy marks lost in multi-correct negative marking (-16 marks)."
            ),
            TestRecord(
                id = 2002,
                testName = "Physics Mechanics Part Test",
                date = "2026-10-11",
                score = 76,
                maxScore = 100,
                accuracyPct = 82,
                negativeMarks = 8,
                physicsScore = 76,
                chemistryScore = 0,
                mathsScore = 0,
                strongTopics = "Rotational Inertia, Circular Dynamics",
                weakTopics = "Variable Mass Systems, Rolling with Slipping",
                aiReport = "Accuracy improved to 82%. Slipping condition friction direction needs targeted re-derivation."
            ),
            TestRecord(
                id = 2003,
                testName = "Electrostatics & Current Minor Test",
                date = "2026-10-15",
                score = 84,
                maxScore = 100,
                accuracyPct = 88,
                negativeMarks = 4,
                physicsScore = 84,
                chemistryScore = 0,
                mathsScore = 0,
                strongTopics = "Gauss Law, Conductor Properties, RC Circuits",
                weakTopics = "Dielectric Slab Fringe Force Calculation",
                aiReport = "Peak performance! Only 4 negative marks lost. Fringe effect tagged for Spaced Repetition."
            )
        )

        val defaultModules = listOf(
            BookModule(id = 3001, bookName = "HC Verma Vol 1", chapter = "Rotational Mechanics", exerciseName = "Objective 1 & 2 + Ex Q1-55", solvedCount = 42, totalCount = 55, status = "In Progress"),
            BookModule(id = 3002, bookName = "Irodov General Physics", chapter = "1.5 Dynamics of Solid Bodies", exerciseName = "Problems 1.234 to 1.280", solvedCount = 28, totalCount = 46, status = "In Progress"),
            BookModule(id = 3003, bookName = "Allen Physics Module", chapter = "Electrostatics & Capacitance", exerciseName = "Exercise 1 + Ex 2 + Adv Archive", solvedCount = 70, totalCount = 70, status = "Completed"),
            BookModule(id = 3004, bookName = "46 Years JEE Adv PYQ", chapter = "Newton's Laws & Friction", exerciseName = "Archive 1978 - 2024", solvedCount = 52, totalCount = 52, status = "Completed")
        )

        val defaultLectures = listOf(
            LectureRecord(id = 4001, subject = "Physics", topic = "Rotational Dynamics Lec 07: Pure Rolling on Incline", lecNumber = 7, durationMins = 95, understandingRating = 5, notesTaken = true, keyTakeaway = "a_cm = g sin θ / (1 + I_cm / mR^2)"),
            LectureRecord(id = 4002, subject = "Physics", topic = "Electrostatics Lec 12: Conductors in External Field & Cavity", lecNumber = 12, durationMins = 85, understandingRating = 4, notesTaken = true, keyTakeaway = "Inner cavity induced charge is exactly -q; lattice shielding holds."),
            LectureRecord(id = 4003, subject = "Chemistry", topic = "Chemical Kinetics Lec 04: Arrhenius Equation", lecNumber = 4, durationMins = 75, understandingRating = 4, notesTaken = true, keyTakeaway = "ln(k2/k1) = Ea/R * (1/T1 - 1/T2)")
        )

        val defaultSyllabus = listOf(
            SyllabusChapter(id = 5001, subject = "Physics", chapterName = "Rotational Dynamics", weightage = "High (8-10%)", theoryCompleted = true, moduleSolved = true, pyqSolved = false, revisionCount = 2),
            SyllabusChapter(id = 5002, subject = "Physics", chapterName = "Electrostatics & Gauss Law", weightage = "High (8-10%)", theoryCompleted = true, moduleSolved = true, pyqSolved = true, revisionCount = 3),
            SyllabusChapter(id = 5003, subject = "Physics", chapterName = "Current Electricity & Magnetism", weightage = "Medium (4-6%)", theoryCompleted = true, moduleSolved = true, pyqSolved = true, revisionCount = 1),
            SyllabusChapter(id = 5004, subject = "Physics", chapterName = "Thermodynamics & KTG", weightage = "High (8-10%)", theoryCompleted = false, moduleSolved = false, pyqSolved = false, revisionCount = 0),
            SyllabusChapter(id = 5005, subject = "Chemistry", chapterName = "Chemical Bonding & Molecular Structure", weightage = "High (8-10%)", theoryCompleted = true, moduleSolved = true, pyqSolved = true, revisionCount = 3),
            SyllabusChapter(id = 5006, subject = "Chemistry", chapterName = "Coordination Compounds", weightage = "High (8-10%)", theoryCompleted = true, moduleSolved = false, pyqSolved = false, revisionCount = 1),
            SyllabusChapter(id = 5007, subject = "Maths", chapterName = "Definite Integration & Area Under Curve", weightage = "High (8-10%)", theoryCompleted = true, moduleSolved = true, pyqSolved = false, revisionCount = 1),
            SyllabusChapter(id = 5008, subject = "Maths", chapterName = "Differential Equations", weightage = "Medium (4-6%)", theoryCompleted = false, moduleSolved = false, pyqSolved = false, revisionCount = 0)
        )

        val defaultAIRevisions = listOf(
            AIRevisionSchedule(
                id = 6001,
                chapterName = "Rotational Dynamics (Rolling & Toppling)",
                subject = "Physics",
                scheduledDate = "Tomorrow",
                triggerReason = "Negative marks in Test 2 (-8 marks in rolling friction constraint) + Spaced Day 3",
                recommendedAction = "Solve 15 Adv PYQs & re-derive angular momentum about instantaneous center of rotation",
                urgency = "High Priority",
                completed = false
            ),
            AIRevisionSchedule(
                id = 6002,
                chapterName = "Dielectric Slab Fringe Force",
                subject = "Physics",
                scheduledDate = "In 3 Days",
                triggerReason = "Tagged as Feynman Knowledge Gap during 3-min concept drill",
                recommendedAction = "Practice fringe effect force proof using F = -dU/dx at constant charge & voltage",
                urgency = "Routine",
                completed = false
            ),
            AIRevisionSchedule(
                id = 6003,
                chapterName = "Coordination Compounds (CFT & Isomerism)",
                subject = "Chemistry",
                scheduledDate = "In 5 Days",
                triggerReason = "Module solving lag (Exercise 2 pending) before weekend mock test",
                recommendedAction = "Complete Allen Ex 2 Q1-30 and crystal field splitting energy calculations",
                urgency = "Pre-Test Polish",
                completed = false
            )
        )

        return LifeOSState(
            config = SystemConfig(
                p3LockEnabled = true,
                pomodoroMins = 45,
                savingsTarget = 45000,
                coachBanner = "Coach: Start 1 P1/Q1 session to unlock lower quadrants!"
            ),
            profile = UserProfile(
                disciplineScore = 78,
                peakSlot = "Early Morning (5 AM - 11 AM)",
                coachTone = "Strict & Direct Reality Check",
                masteryHours = 1420,
                wastedHoursWeek = 4.5,
                focusedHoursWeek = 26.5,
                morningPrime = "Deliver top-tier Physics concepts & scale footwear brand to Gen-Z.",
                nightDump = "Clear backlog on Electrostatics sheet & review packaging vendor quotes."
            ),
            tasks = listOf(
                TaskItem(101, "Design Rotational Dynamics Advanced Problem Set (Level 1-3)", "P1", "10k", "high", "Q1", false),
                TaskItem(102, "Record 3-Min Concept Hook & Live Stream Prep", "P1", "10k", "high", "Q2", true),
                TaskItem(103, "Review Gen-Z Brand Campaign & Footwear Mockups", "P2", "10k", "med", "Q2", false),
                TaskItem(104, "Organize Student Feedback Sheet & Routine Emails", "P3", "100", "low", "Q3", false),
                TaskItem(105, "Unscheduled social browsing & random WhatsApp replies", "P3", "100", "low", "Q4", false)
            ),
            futureGoals = defaultFutureGoals,
            targets = listOf(
                TargetDeadline(201, "Complete Mechanics & Electrostatics Module", "2026-10-30", "Academic", 20, 13),
                TargetDeadline(202, "Q4 Festive Brand Relaunch & 100 Orders Milestone", "2026-11-15", "Financial", 100, 62),
                TargetDeadline(203, "All-India JEE Adv Mock Test Benchmark 220+", "2026-11-05", "Academic", 5, 2)
            ),
            spacedQueue = listOf(
                SpacedRepetitionItem(301, "Variable Mass Systems & Rocket Propulsion", "Day 3 Review", "Today"),
                SpacedRepetitionItem(302, "Gauss Law & Non-Uniform Charge Density", "Day 7 Review", "Today"),
                SpacedRepetitionItem(303, "Pseudo Force in Non-Inertial Frames", "Day 21 Review", "Tomorrow")
            ),
            habits = listOf(
                HabitItem(401, "Morning Prime + 6:00 AM Wake-up", "good", "+38% Deep Focus Hrs", 12, true),
                HabitItem(402, "90-Min Zero-Phone Deep Teaching Prep", "good", "+42% P1 Completion", 9, true),
                HabitItem(403, "Late Night Reels / Scrolling (Post 11:30 PM)", "bad", "-2.1 hrs Next Day Focus", 0, false)
            ),
            subjects = listOf(
                AcademicSubject("Rotational Mechanics & Variable Mass", 78, 80),
                AcademicSubject("Electrostatics & Current Electricity", 64, 72),
                AcademicSubject("Work, Power, Energy & Circular Motion", 92, 90)
            ),
            vault = listOf(
                VaultItem(501, "Analogy: Explain Moment of Inertia using a heavy vs light दरवाजा (hinge distance = r²)"),
                VaultItem(502, "Common Doubt: Why normal reaction shifts during toppling on an inclined plane")
            ),
            knowledgeGaps = listOf("Rolling with slipping energy transition proof", "Dielectric slab force fringe effect"),
            finance = FinanceData(saved = 31200, impulse = 2450, skillInvest = 4200),
            bizIdeas = listOf(
                BusinessIdea(601, "Pair traditional handcrafted jootis with oversized cargos/denim in 15-sec reels", "Execution"),
                BusinessIdea(602, "Follow up with Creator Lab contacts & packaging supplier (Due in 2 days)", "CRM Radar")
            ),
            decisions = listOf(
                DecisionItem(701, "Introduced 4-Tier Scaffolded Worksheets for JEE Adv", "Higher student problem-solving confidence", "Apr 2027")
            ),
            questions = defaultQuestions,

            // Study Hub state
            geminiApiKey = "",
            tests = defaultTests,
            bookModules = defaultModules,
            lectures = defaultLectures,
            syllabusChapters = defaultSyllabus,
            aiRevisionSchedules = defaultAIRevisions,
            latestAiAudit = "",
            isAiAnalyzing = false,
            notificationsEnabled = true
        )
    }

    private fun mutate(block: (LifeOSState) -> LifeOSState) {
        val newState = block(_state.value)
        _state.value = newState
        saveStateToDisk(newState)
    }

    // Task Actions
    fun addTask(title: String, priority: String, leverage: String, energy: String, quadrant: String = "Q1") {
        mutate { s ->
            val mappedPrio = when (quadrant) {
                "Q1" -> "P1"
                "Q2" -> "P1"
                "Q3" -> "P2"
                else -> "P3"
            }
            val mappedLev = if (quadrant == "Q2" || quadrant == "Q1") "10k" else leverage
            s.copy(tasks = listOf(TaskItem(System.currentTimeMillis(), title, mappedPrio, mappedLev, energy, quadrant, false)) + s.tasks)
        }
    }

    fun toggleTask(id: Long) {
        mutate { s ->
            s.copy(tasks = s.tasks.map { if (it.id == id) it.copy(done = !it.done) else it })
        }
    }

    fun deleteTask(id: Long) {
        mutate { s -> s.copy(tasks = s.tasks.filter { it.id != id }) }
    }

    fun moveTaskQuadrant(id: Long, newQuadrant: String) {
        mutate { s ->
            s.copy(tasks = s.tasks.map {
                if (it.id == id) {
                    val newPrio = when (newQuadrant) {
                        "Q1" -> "P1"
                        "Q2" -> "P1"
                        "Q3" -> "P2"
                        else -> "P3"
                    }
                    it.copy(quadrant = newQuadrant, priority = newPrio)
                } else it
            })
        }
    }

    // Future Goal Actions (Workflow vs Ideal)
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
        mutate { s ->
            val newGoal = FutureGoal(
                id = System.currentTimeMillis(),
                title = title,
                category = category,
                targetHorizon = targetHorizon,
                targetDate = targetDate,
                currentMetric = currentMetric,
                idealMetric = idealMetric,
                currentProgress = 30,
                idealProgress = 80,
                currentTrajectoryDesc = currentTrajectory,
                idealTrajectoryDesc = idealTrajectory,
                gapSummary = gapSummary,
                bridgeTheGapAction = bridgeAction,
                keyMilestones = listOf("Initial Baseline Set", "Core Workflow Established", "Full Scaled Benchmark Reached"),
                completedMilestonesCount = 1
            )
            s.copy(futureGoals = listOf(newGoal) + s.futureGoals)
        }
    }

    fun toggleGoalMilestone(goalId: Long) {
        mutate { s ->
            s.copy(futureGoals = s.futureGoals.map { g ->
                if (g.id == goalId) {
                    val nextCount = if (g.completedMilestonesCount < g.keyMilestones.size) {
                        g.completedMilestonesCount + 1
                    } else 0
                    val nextPct = if (g.keyMilestones.isNotEmpty()) {
                        (nextCount * 100 / g.keyMilestones.size)
                    } else g.currentProgress
                    g.copy(completedMilestonesCount = nextCount, currentProgress = nextPct)
                } else g
            })
        }
    }

    fun updateGoalProgress(goalId: Long, newProgress: Int) {
        mutate { s ->
            s.copy(futureGoals = s.futureGoals.map {
                if (it.id == goalId) it.copy(currentProgress = newProgress.coerceIn(0, 100)) else it
            })
        }
    }

    fun deleteFutureGoal(id: Long) {
        mutate { s -> s.copy(futureGoals = s.futureGoals.filter { it.id != id }) }
    }

    // STUDY HUB ACTIONS: Tests
    fun addTestRecord(test: TestRecord) {
        mutate { s -> s.copy(tests = listOf(test) + s.tests) }
    }

    fun deleteTestRecord(id: Long) {
        mutate { s -> s.copy(tests = s.tests.filter { it.id != id }) }
    }

    // STUDY HUB ACTIONS: Modules & Books
    fun addBookModule(module: BookModule) {
        mutate { s -> s.copy(bookModules = listOf(module) + s.bookModules) }
    }

    fun updateModuleProgress(id: Long, solved: Int, status: String) {
        mutate { s ->
            s.copy(bookModules = s.bookModules.map {
                if (it.id == id) it.copy(solvedCount = solved, status = status) else it
            })
        }
    }

    fun deleteBookModule(id: Long) {
        mutate { s -> s.copy(bookModules = s.bookModules.filter { it.id != id }) }
    }

    // STUDY HUB ACTIONS: Lectures
    fun addLectureRecord(lecture: LectureRecord) {
        mutate { s -> s.copy(lectures = listOf(lecture) + s.lectures) }
    }

    fun deleteLectureRecord(id: Long) {
        mutate { s -> s.copy(lectures = s.lectures.filter { it.id != id }) }
    }

    // STUDY HUB ACTIONS: Syllabus
    fun toggleSyllabusStatus(id: Long, field: String) {
        mutate { s ->
            s.copy(syllabusChapters = s.syllabusChapters.map { ch ->
                if (ch.id == id) {
                    when (field) {
                        "theory" -> ch.copy(theoryCompleted = !ch.theoryCompleted)
                        "module" -> ch.copy(moduleSolved = !ch.moduleSolved)
                        "pyq" -> ch.copy(pyqSolved = !ch.pyqSolved)
                        "revision" -> ch.copy(revisionCount = ch.revisionCount + 1)
                        else -> ch
                    }
                } else ch
            })
        }
    }

    fun addSyllabusChapter(chapter: SyllabusChapter) {
        mutate { s -> s.copy(syllabusChapters = s.syllabusChapters + chapter) }
    }

    fun deleteSyllabusChapter(id: Long) {
        mutate { s -> s.copy(syllabusChapters = s.syllabusChapters.filter { it.id != id }) }
    }

    // STUDY HUB ACTIONS: AI Revision
    fun toggleAIRevisionDone(id: Long) {
        mutate { s ->
            s.copy(aiRevisionSchedules = s.aiRevisionSchedules.map {
                if (it.id == id) it.copy(completed = !it.completed) else it
            })
        }
    }

    fun addAIRevisionSchedule(schedule: AIRevisionSchedule) {
        mutate { s -> s.copy(aiRevisionSchedules = listOf(schedule) + s.aiRevisionSchedules) }
    }

    fun deleteAIRevisionSchedule(id: Long) {
        mutate { s -> s.copy(aiRevisionSchedules = s.aiRevisionSchedules.filter { it.id != id }) }
    }

    // Gemini API Key & Audit
    fun setGeminiApiKey(key: String) {
        mutate { s -> s.copy(geminiApiKey = key.trim()) }
    }

    fun setLatestAiAudit(report: String) {
        mutate { s -> s.copy(latestAiAudit = report, isAiAnalyzing = false) }
    }

    fun setAiAnalyzing(analyzing: Boolean) {
        mutate { s -> s.copy(isAiAnalyzing = analyzing) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        mutate { s -> s.copy(notificationsEnabled = enabled) }
    }

    fun autoRescheduleBacklog() {
        mutate { s ->
            val updated = s.targets.map { t ->
                val rem = (t.totalUnits - t.completedUnits).coerceAtLeast(0)
                val daily = (rem / 10.0).coerceAtLeast(0.5)
                t.copy(recalculatedDaily = daily)
            }
            s.copy(
                targets = updated,
                config = s.config.copy(coachBanner = "✨ Backlog Auto-Rescheduled: Distributed evenly across next 10 days.")
            )
        }
    }

    // Habit Actions
    fun toggleHabit(id: Long) {
        mutate { s ->
            s.copy(habits = s.habits.map { h ->
                if (h.id == id) {
                    val nextDone = !h.doneToday
                    val nextStreak = if (nextDone) h.streak + 1 else (h.streak - 1).coerceAtLeast(0)
                    h.copy(doneToday = nextDone, streak = nextStreak)
                } else h
            })
        }
    }

    fun addHabit(name: String, type: String, impact: String) {
        mutate { s ->
            s.copy(habits = s.habits + HabitItem(System.currentTimeMillis(), name, type, impact, 0, false))
        }
    }

    // Spaced Repetition Actions
    fun completeSpacedItem(id: Long) {
        mutate { s -> s.copy(spacedQueue = s.spacedQueue.filter { it.id != id }) }
    }

    fun addSpacedTopic(topic: String) {
        mutate { s ->
            s.copy(spacedQueue = listOf(SpacedRepetitionItem(System.currentTimeMillis(), topic, "Day 3 Auto-Scheduled", "In 3 Days")) + s.spacedQueue)
        }
    }

    // Deadlines Actions
    fun addTarget(title: String, deadline: String, pillar: String, totalUnits: Int) {
        mutate { s ->
            s.copy(targets = listOf(TargetDeadline(System.currentTimeMillis(), title, deadline, pillar, totalUnits, 0)) + s.targets)
        }
    }

    fun incrementTargetUnit(id: Long) {
        mutate { s ->
            s.copy(targets = s.targets.map { t ->
                if (t.id == id && t.completedUnits < t.totalUnits) {
                    t.copy(completedUnits = t.completedUnits + 1)
                } else t
            })
        }
    }

    fun deleteTarget(id: Long) {
        mutate { s -> s.copy(targets = s.targets.filter { it.id != id }) }
    }

    // Pillars Actions
    fun addVaultItem(text: String) {
        mutate { s -> s.copy(vault = listOf(VaultItem(System.currentTimeMillis(), text)) + s.vault) }
    }

    fun addKnowledgeGap(gap: String) {
        mutate { s ->
            val newGaps = s.knowledgeGaps + gap
            val newSpaced = listOf(
                SpacedRepetitionItem(System.currentTimeMillis(), "$gap (Feynman Gap)", "Day 1 Immediate", "Today")
            ) + s.spacedQueue
            s.copy(knowledgeGaps = newGaps, spacedQueue = newSpaced)
        }
    }

    fun logExpense(amount: Int, category: String) {
        mutate { s ->
            val cur = s.finance
            val updated = when (category) {
                "impulse" -> cur.copy(impulse = cur.impulse + amount)
                "skill" -> cur.copy(saved = cur.saved + amount)
                else -> cur
            }
            s.copy(finance = updated)
        }
    }

    fun addBusinessIdea(text: String, stage: String = "Validation") {
        mutate { s -> s.copy(bizIdeas = listOf(BusinessIdea(System.currentTimeMillis(), text, stage)) + s.bizIdeas) }
    }

    fun addDecision(title: String, outcome: String, reviewDate: String) {
        mutate { s -> s.copy(decisions = listOf(DecisionItem(System.currentTimeMillis(), title, outcome, reviewDate)) + s.decisions) }
    }

    fun updateSubjectProgress(subjectName: String, newDone: Int) {
        mutate { s ->
            s.copy(subjects = s.subjects.map {
                if (it.name == subjectName) it.copy(done = newDone.coerceIn(0, 100)) else it
            })
        }
    }

    // Timer & Session Logging
    fun logFocusSessionCompleted(hours: Double = 0.75) {
        mutate { s ->
            s.copy(
                profile = s.profile.copy(
                    focusedHoursWeek = (s.profile.focusedHoursWeek + hours),
                    masteryHours = s.profile.masteryHours + 1
                )
            )
        }
    }

    fun logWastedHours(hours: Double) {
        mutate { s -> s.copy(profile = s.profile.copy(wastedHoursWeek = (s.profile.wastedHoursWeek + hours))) }
    }

    fun addMasteryHours(hours: Int) {
        mutate { s -> s.copy(profile = s.profile.copy(masteryHours = s.profile.masteryHours + hours)) }
    }

    // Profile & Prime
    fun saveMorningPrime(prime: String) {
        mutate { s -> s.copy(profile = s.profile.copy(morningPrime = prime)) }
    }

    fun saveNightDump(dump: String) {
        mutate { s -> s.copy(profile = s.profile.copy(nightDump = dump)) }
    }

    fun updateDiagnosticResults(disciplineScore: Int, coachTone: String) {
        mutate { s -> s.copy(profile = s.profile.copy(disciplineScore = disciplineScore, coachTone = coachTone)) }
    }

    // Admin & Broadcast Config
    fun sendBroadcast(message: String) {
        mutate { s -> s.copy(config = s.config.copy(coachBanner = message)) }
    }

    fun updateSystemConfig(p3Lock: Boolean, pomodoroMins: Int, savingsTarget: Int) {
        mutate { s ->
            s.copy(config = s.config.copy(
                p3LockEnabled = p3Lock,
                pomodoroMins = pomodoroMins,
                savingsTarget = savingsTarget
            ))
        }
    }

    fun addAdminQuestion(question: String, level: String) {
        mutate { s ->
            val nextId = System.currentTimeMillis()
            val newQ = DiagnosticQuestion(
                nextId, level, "${s.questions.size + 1}. $question",
                listOf(DiagnosticOption("High Discipline Response", 10), DiagnosticOption("Moderate Response", 6), DiagnosticOption("Needs Improvement", 2))
            )
            s.copy(questions = s.questions + newQ)
        }
    }

    fun deleteAdminQuestion(id: Long) {
        mutate { s -> s.copy(questions = s.questions.filter { it.id != id }) }
    }

    fun resetDemoDatabase() {
        val def = createDefaultState()
        _state.value = def
        saveStateToDisk(def)
    }

    fun exportDatabaseJson(): String {
        return serializeStateToJson(_state.value)
    }

    // JSON Persistence Helper
    private fun saveStateToDisk(state: LifeOSState) {
        try {
            val json = serializeStateToJson(state)
            prefs.edit().putString("db_snapshot", json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun serializeStateToJson(state: LifeOSState): String {
        val root = JSONObject()
        val cfg = JSONObject().apply {
            put("p3LockEnabled", state.config.p3LockEnabled)
            put("pomodoroMins", state.config.pomodoroMins)
            put("savingsTarget", state.config.savingsTarget)
            put("coachBanner", state.config.coachBanner)
        }
        root.put("config", cfg)

        val prof = JSONObject().apply {
            put("disciplineScore", state.profile.disciplineScore)
            put("peakSlot", state.profile.peakSlot)
            put("coachTone", state.profile.coachTone)
            put("masteryHours", state.profile.masteryHours)
            put("wastedHoursWeek", state.profile.wastedHoursWeek)
            put("focusedHoursWeek", state.profile.focusedHoursWeek)
            put("morningPrime", state.profile.morningPrime)
            put("nightDump", state.profile.nightDump)
        }
        root.put("profile", prof)
        root.put("geminiApiKey", state.geminiApiKey)
        root.put("latestAiAudit", state.latestAiAudit)
        root.put("notificationsEnabled", state.notificationsEnabled)

        val tasksArr = JSONArray()
        state.tasks.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("priority", t.priority)
                put("leverage", t.leverage)
                put("energy", t.energy)
                put("quadrant", t.quadrant)
                put("done", t.done)
            }
            tasksArr.put(obj)
        }
        root.put("tasks", tasksArr)

        val goalsArr = JSONArray()
        state.futureGoals.forEach { g ->
            val obj = JSONObject().apply {
                put("id", g.id)
                put("title", g.title)
                put("category", g.category)
                put("targetHorizon", g.targetHorizon)
                put("targetDate", g.targetDate)
                put("currentMetric", g.currentMetric)
                put("idealMetric", g.idealMetric)
                put("currentProgress", g.currentProgress)
                put("idealProgress", g.idealProgress)
                put("currentTrajectoryDesc", g.currentTrajectoryDesc)
                put("idealTrajectoryDesc", g.idealTrajectoryDesc)
                put("gapSummary", g.gapSummary)
                put("bridgeTheGapAction", g.bridgeTheGapAction)
                val mArr = JSONArray()
                g.keyMilestones.forEach { mArr.put(it) }
                put("keyMilestones", mArr)
                put("completedMilestonesCount", g.completedMilestonesCount)
            }
            goalsArr.put(obj)
        }
        root.put("futureGoals", goalsArr)

        // Tests
        val testsArr = JSONArray()
        state.tests.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("testName", t.testName)
                put("date", t.date)
                put("score", t.score)
                put("maxScore", t.maxScore)
                put("accuracyPct", t.accuracyPct)
                put("negativeMarks", t.negativeMarks)
                put("physicsScore", t.physicsScore)
                put("chemistryScore", t.chemistryScore)
                put("mathsScore", t.mathsScore)
                put("strongTopics", t.strongTopics)
                put("weakTopics", t.weakTopics)
                put("aiReport", t.aiReport)
            }
            testsArr.put(obj)
        }
        root.put("tests", testsArr)

        // Modules
        val modArr = JSONArray()
        state.bookModules.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("bookName", m.bookName)
                put("chapter", m.chapter)
                put("exerciseName", m.exerciseName)
                put("solvedCount", m.solvedCount)
                put("totalCount", m.totalCount)
                put("status", m.status)
            }
            modArr.put(obj)
        }
        root.put("bookModules", modArr)

        // Lectures
        val lecArr = JSONArray()
        state.lectures.forEach { l ->
            val obj = JSONObject().apply {
                put("id", l.id)
                put("subject", l.subject)
                put("topic", l.topic)
                put("lecNumber", l.lecNumber)
                put("durationMins", l.durationMins)
                put("understandingRating", l.understandingRating)
                put("notesTaken", l.notesTaken)
                put("keyTakeaway", l.keyTakeaway)
            }
            lecArr.put(obj)
        }
        root.put("lectures", lecArr)

        // Syllabus
        val sylArr = JSONArray()
        state.syllabusChapters.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("subject", s.subject)
                put("chapterName", s.chapterName)
                put("weightage", s.weightage)
                put("theoryCompleted", s.theoryCompleted)
                put("moduleSolved", s.moduleSolved)
                put("pyqSolved", s.pyqSolved)
                put("revisionCount", s.revisionCount)
            }
            sylArr.put(obj)
        }
        root.put("syllabusChapters", sylArr)

        // AI Revision
        val revArr = JSONArray()
        state.aiRevisionSchedules.forEach { r ->
            val obj = JSONObject().apply {
                put("id", r.id)
                put("chapterName", r.chapterName)
                put("subject", r.subject)
                put("scheduledDate", r.scheduledDate)
                put("triggerReason", r.triggerReason)
                put("recommendedAction", r.recommendedAction)
                put("urgency", r.urgency)
                put("completed", r.completed)
            }
            revArr.put(obj)
        }
        root.put("aiRevisionSchedules", revArr)

        val targetsArr = JSONArray()
        state.targets.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("deadline", t.deadline)
                put("pillar", t.pillar)
                put("totalUnits", t.totalUnits)
                put("completedUnits", t.completedUnits)
                if (t.recalculatedDaily != null) put("recalculatedDaily", t.recalculatedDaily)
            }
            targetsArr.put(obj)
        }
        root.put("targets", targetsArr)

        val spacedArr = JSONArray()
        state.spacedQueue.forEach { s ->
            val obj = JSONObject().apply {
                put("id", s.id)
                put("topic", s.topic)
                put("stage", s.stage)
                put("due", s.due)
            }
            spacedArr.put(obj)
        }
        root.put("spacedQueue", spacedArr)

        val habitArr = JSONArray()
        state.habits.forEach { h ->
            val obj = JSONObject().apply {
                put("id", h.id)
                put("name", h.name)
                put("type", h.type)
                put("impact", h.impact)
                put("streak", h.streak)
                put("doneToday", h.doneToday)
            }
            habitArr.put(obj)
        }
        root.put("habits", habitArr)

        val finObj = JSONObject().apply {
            put("saved", state.finance.saved)
            put("impulse", state.finance.impulse)
            put("skillInvest", state.finance.skillInvest)
        }
        root.put("finance", finObj)

        val gapsArr = JSONArray()
        state.knowledgeGaps.forEach { gapsArr.put(it) }
        root.put("knowledgeGaps", gapsArr)

        return root.toString(2)
    }

    private fun parseJsonToState(json: String): LifeOSState {
        val root = JSONObject(json)
        val def = createDefaultState()

        val cfgObj = root.optJSONObject("config")
        val config = if (cfgObj != null) {
            SystemConfig(
                p3LockEnabled = cfgObj.optBoolean("p3LockEnabled", true),
                pomodoroMins = cfgObj.optInt("pomodoroMins", 45),
                savingsTarget = cfgObj.optInt("savingsTarget", 45000),
                coachBanner = cfgObj.optString("coachBanner", def.config.coachBanner)
            )
        } else def.config

        val profObj = root.optJSONObject("profile")
        val profile = if (profObj != null) {
            UserProfile(
                disciplineScore = profObj.optInt("disciplineScore", 78),
                peakSlot = profObj.optString("peakSlot", "Morning"),
                coachTone = profObj.optString("coachTone", "Strict & Direct Reality Check"),
                masteryHours = profObj.optInt("masteryHours", 1420),
                wastedHoursWeek = profObj.optDouble("wastedHoursWeek", 4.5),
                focusedHoursWeek = profObj.optDouble("focusedHoursWeek", 26.5),
                morningPrime = profObj.optString("morningPrime", def.profile.morningPrime),
                nightDump = profObj.optString("nightDump", def.profile.nightDump)
            )
        } else def.profile

        val geminiApiKey = root.optString("geminiApiKey", "")
        val latestAiAudit = root.optString("latestAiAudit", "")
        val notifEnabled = root.optBoolean("notificationsEnabled", true)

        val tasks = mutableListOf<TaskItem>()
        val tasksArr = root.optJSONArray("tasks")
        if (tasksArr != null) {
            for (i in 0 until tasksArr.length()) {
                val o = tasksArr.getJSONObject(i)
                val prio = o.optString("priority", "P1")
                val quad = o.optString("quadrant", if (prio == "P1") "Q1" else if (prio == "P2") "Q2" else "Q3")
                tasks.add(
                    TaskItem(
                        id = o.optLong("id", System.currentTimeMillis()),
                        title = o.optString("title"),
                        priority = prio,
                        leverage = o.optString("leverage", "10k"),
                        energy = o.optString("energy", "high"),
                        quadrant = quad,
                        done = o.optBoolean("done", false)
                    )
                )
            }
        } else tasks.addAll(def.tasks)

        val goals = mutableListOf<FutureGoal>()
        val goalsArr = root.optJSONArray("futureGoals")
        if (goalsArr != null) {
            for (i in 0 until goalsArr.length()) {
                val o = goalsArr.getJSONObject(i)
                val mList = mutableListOf<String>()
                val mArr = o.optJSONArray("keyMilestones")
                if (mArr != null) {
                    for (j in 0 until mArr.length()) {
                        mList.add(mArr.getString(j))
                    }
                }
                goals.add(
                    FutureGoal(
                        id = o.optLong("id", System.currentTimeMillis()),
                        title = o.optString("title"),
                        category = o.optString("category", "Academic"),
                        targetHorizon = o.optString("targetHorizon", "90 Days"),
                        targetDate = o.optString("targetDate", "2026-12-31"),
                        currentMetric = o.optString("currentMetric"),
                        idealMetric = o.optString("idealMetric"),
                        currentProgress = o.optInt("currentProgress", 50),
                        idealProgress = o.optInt("idealProgress", 80),
                        currentTrajectoryDesc = o.optString("currentTrajectoryDesc"),
                        idealTrajectoryDesc = o.optString("idealTrajectoryDesc"),
                        gapSummary = o.optString("gapSummary"),
                        bridgeTheGapAction = o.optString("bridgeTheGapAction"),
                        keyMilestones = if (mList.isNotEmpty()) mList else listOf("Phase 1 Complete", "Core Benchmark Met"),
                        completedMilestonesCount = o.optInt("completedMilestonesCount", 1)
                    )
                )
            }
        } else goals.addAll(def.futureGoals)

        // Tests
        val tests = mutableListOf<TestRecord>()
        val testsArr = root.optJSONArray("tests")
        if (testsArr != null) {
            for (i in 0 until testsArr.length()) {
                val o = testsArr.getJSONObject(i)
                tests.add(
                    TestRecord(
                        id = o.optLong("id", System.currentTimeMillis()),
                        testName = o.optString("testName"),
                        date = o.optString("date"),
                        score = o.optInt("score"),
                        maxScore = o.optInt("maxScore", 300),
                        accuracyPct = o.optInt("accuracyPct", 80),
                        negativeMarks = o.optInt("negativeMarks", 0),
                        physicsScore = o.optInt("physicsScore", 0),
                        chemistryScore = o.optInt("chemistryScore", 0),
                        mathsScore = o.optInt("mathsScore", 0),
                        strongTopics = o.optString("strongTopics"),
                        weakTopics = o.optString("weakTopics"),
                        aiReport = o.optString("aiReport")
                    )
                )
            }
        } else tests.addAll(def.tests)

        // Modules
        val modules = mutableListOf<BookModule>()
        val modArr = root.optJSONArray("bookModules")
        if (modArr != null) {
            for (i in 0 until modArr.length()) {
                val o = modArr.getJSONObject(i)
                modules.add(
                    BookModule(
                        id = o.optLong("id", System.currentTimeMillis()),
                        bookName = o.optString("bookName"),
                        chapter = o.optString("chapter"),
                        exerciseName = o.optString("exerciseName"),
                        solvedCount = o.optInt("solvedCount", 0),
                        totalCount = o.optInt("totalCount", 50),
                        status = o.optString("status", "In Progress")
                    )
                )
            }
        } else modules.addAll(def.bookModules)

        // Lectures
        val lectures = mutableListOf<LectureRecord>()
        val lecArr = root.optJSONArray("lectures")
        if (lecArr != null) {
            for (i in 0 until lecArr.length()) {
                val o = lecArr.getJSONObject(i)
                lectures.add(
                    LectureRecord(
                        id = o.optLong("id", System.currentTimeMillis()),
                        subject = o.optString("subject"),
                        topic = o.optString("topic"),
                        lecNumber = o.optInt("lecNumber", 1),
                        durationMins = o.optInt("durationMins", 60),
                        understandingRating = o.optInt("understandingRating", 5),
                        notesTaken = o.optBoolean("notesTaken", true),
                        keyTakeaway = o.optString("keyTakeaway")
                    )
                )
            }
        } else lectures.addAll(def.lectures)

        // Syllabus
        val syllabus = mutableListOf<SyllabusChapter>()
        val sylArr = root.optJSONArray("syllabusChapters")
        if (sylArr != null) {
            for (i in 0 until sylArr.length()) {
                val o = sylArr.getJSONObject(i)
                syllabus.add(
                    SyllabusChapter(
                        id = o.optLong("id", System.currentTimeMillis()),
                        subject = o.optString("subject"),
                        chapterName = o.optString("chapterName"),
                        weightage = o.optString("weightage", "High (8-10%)"),
                        theoryCompleted = o.optBoolean("theoryCompleted", false),
                        moduleSolved = o.optBoolean("moduleSolved", false),
                        pyqSolved = o.optBoolean("pyqSolved", false),
                        revisionCount = o.optInt("revisionCount", 0)
                    )
                )
            }
        } else syllabus.addAll(def.syllabusChapters)

        // AI Revision
        val aiRevisions = mutableListOf<AIRevisionSchedule>()
        val revArr = root.optJSONArray("aiRevisionSchedules")
        if (revArr != null) {
            for (i in 0 until revArr.length()) {
                val o = revArr.getJSONObject(i)
                aiRevisions.add(
                    AIRevisionSchedule(
                        id = o.optLong("id", System.currentTimeMillis()),
                        chapterName = o.optString("chapterName"),
                        subject = o.optString("subject"),
                        scheduledDate = o.optString("scheduledDate"),
                        triggerReason = o.optString("triggerReason"),
                        recommendedAction = o.optString("recommendedAction"),
                        urgency = o.optString("urgency", "High Priority"),
                        completed = o.optBoolean("completed", false)
                    )
                )
            }
        } else aiRevisions.addAll(def.aiRevisionSchedules)

        val targets = mutableListOf<TargetDeadline>()
        val targetsArr = root.optJSONArray("targets")
        if (targetsArr != null) {
            for (i in 0 until targetsArr.length()) {
                val o = targetsArr.getJSONObject(i)
                targets.add(
                    TargetDeadline(
                        id = o.optLong("id", System.currentTimeMillis()),
                        title = o.optString("title"),
                        deadline = o.optString("deadline", "2026-10-30"),
                        pillar = o.optString("pillar", "Academic"),
                        totalUnits = o.optInt("totalUnits", 20),
                        completedUnits = o.optInt("completedUnits", 0),
                        recalculatedDaily = if (o.has("recalculatedDaily")) o.optDouble("recalculatedDaily") else null
                    )
                )
            }
        } else targets.addAll(def.targets)

        val spaced = mutableListOf<SpacedRepetitionItem>()
        val spacedArr = root.optJSONArray("spacedQueue")
        if (spacedArr != null) {
            for (i in 0 until spacedArr.length()) {
                val o = spacedArr.getJSONObject(i)
                spaced.add(
                    SpacedRepetitionItem(
                        id = o.optLong("id", System.currentTimeMillis()),
                        topic = o.optString("topic"),
                        stage = o.optString("stage", "Day 3 Review"),
                        due = o.optString("due", "Today")
                    )
                )
            }
        } else spaced.addAll(def.spacedQueue)

        val habits = mutableListOf<HabitItem>()
        val habitArr = root.optJSONArray("habits")
        if (habitArr != null) {
            for (i in 0 until habitArr.length()) {
                val o = habitArr.getJSONObject(i)
                habits.add(
                    HabitItem(
                        id = o.optLong("id", System.currentTimeMillis()),
                        name = o.optString("name"),
                        type = o.optString("type", "good"),
                        impact = o.optString("impact"),
                        streak = o.optInt("streak", 0),
                        doneToday = o.optBoolean("doneToday", false)
                    )
                )
            }
        } else habits.addAll(def.habits)

        return def.copy(
            config = config,
            profile = profile,
            tasks = tasks,
            futureGoals = goals,
            targets = targets,
            spacedQueue = spaced,
            habits = habits,
            geminiApiKey = geminiApiKey,
            tests = tests,
            bookModules = modules,
            lectures = lectures,
            syllabusChapters = syllabus,
            aiRevisionSchedules = aiRevisions,
            latestAiAudit = latestAiAudit,
            notificationsEnabled = notifEnabled
        )
    }
}
