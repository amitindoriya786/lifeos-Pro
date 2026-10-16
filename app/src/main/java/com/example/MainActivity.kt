package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.LifeOSBg
import com.example.ui.theme.LifeOSProTheme
import com.example.ui.viewmodel.LifeOSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeOSProTheme {
                val viewModel: LifeOSViewModel = viewModel()
                LifeOSMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LifeOSMainApp(viewModel: LifeOSViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val workspaceMode by viewModel.workspaceMode.collectAsStateWithLifecycle()
    val mobileTab by viewModel.mobileTab.collectAsStateWithLifecycle()
    val pillarSubTab by viewModel.pillarSubTab.collectAsStateWithLifecycle()
    val energyFilter by viewModel.energyFilter.collectAsStateWithLifecycle()
    val matrixFilter by viewModel.matrixQuadrantFilter.collectAsStateWithLifecycle()

    val clockTime by viewModel.currentTimeString.collectAsStateWithLifecycle()
    val countdownText by viewModel.nearestDeadlineCountdown.collectAsStateWithLifecycle()

    val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

    val showDiagnostic by viewModel.showDiagnosticModal.collectAsStateWithLifecycle()
    val showPrimeType by viewModel.showPrimeModal.collectAsStateWithLifecycle()
    val showWastedModal by viewModel.showWastedTimeModal.collectAsStateWithLifecycle()
    val showAddGoal by viewModel.showAddGoalModal.collectAsStateWithLifecycle()
    val snackMsg by viewModel.snackMessage.collectAsStateWithLifecycle()

    var showAddSpacedModal by remember { mutableStateOf(false) }
    var showAddHabitModal by remember { mutableStateOf(false) }
    var exportJsonString by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(snackMsg) {
        snackMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnack()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LifeOSBg),
        containerColor = LifeOSBg,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 70.dp)
            )
        },
        topBar = {
            Column {
                TopModeBar(
                    workspaceMode = workspaceMode,
                    onModeChange = { viewModel.setWorkspaceMode(it) },
                    onOpenDiagnostic = { viewModel.openDiagnosticModal() }
                )
                if (workspaceMode == "mobile") {
                    TopLiveHUD(
                        state = state,
                        clockTime = clockTime,
                        countdownText = countdownText,
                        currentEnergyFilter = energyFilter,
                        onEnergyFilterChange = { viewModel.setEnergyFilter(it) },
                        onSprintTrigger = { viewModel.trigger2MinuteSprint() }
                    )
                }
            }
        },
        bottomBar = {
            if (workspaceMode == "mobile") {
                LifeOSBottomNavBar(
                    selectedTab = mobileTab,
                    onTabSelected = { viewModel.setMobileTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = workspaceMode,
                label = "workspaceSwitch"
            ) { mode ->
                if (mode == "admin") {
                    AdminPanelScreen(
                        state = state,
                        onSendBroadcast = { viewModel.sendBroadcast(it) },
                        onUpdateConfig = { p3, pom, sav -> viewModel.updateSystemConfig(p3, pom, sav) },
                        onAddQuestion = { q, lvl -> viewModel.addAdminQuestion(q, lvl) },
                        onDeleteQuestion = { viewModel.deleteAdminQuestion(it) },
                        onResetDatabase = { viewModel.resetDemoDatabase() },
                        onExportJson = { exportJsonString = viewModel.exportDatabaseJson() }
                    )
                } else {
                    AnimatedContent(
                        targetState = mobileTab,
                        label = "mobileTabSwitch"
                    ) { tab ->
                        when (tab) {
                            "home" -> SimpleHomeScreen(
                                state = state,
                                onNavigateTab = { viewModel.setMobileTab(it) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onSprintTrigger = { viewModel.trigger2MinuteSprint() },
                                onOpenPrimeModal = { viewModel.openPrimeModal(it) },
                                onSendNotificationTest = {
                                    viewModel.sendStudyNotification(
                                        "🎯 LifeOS Priority Alert",
                                        "Top 1% Goal Active: Stay consistent on your daily study & high-leverage work!"
                                    )
                                }
                            )

                            "study" -> StudyHubScreen(
                                state = state,
                                onRunAiAudit = { viewModel.runAiStudyAudit() },
                                onSaveApiKey = { viewModel.saveGeminiApiKey(it) },
                                onSendNotification = {
                                    viewModel.sendStudyNotification(
                                        "🎯 LifeOS Study Alert",
                                        "Time for Rotational Dynamics revision & test prep drill!"
                                    )
                                },
                                onAddTest = { viewModel.addTest(it) },
                                onDeleteTest = { viewModel.deleteTest(it) },
                                onAddModule = { viewModel.addModule(it) },
                                onUpdateModule = { id, s, st -> viewModel.updateModule(id, s, st) },
                                onDeleteModule = { viewModel.deleteModule(it) },
                                onAddLecture = { viewModel.addLecture(it) },
                                onDeleteLecture = { viewModel.deleteLecture(it) },
                                onToggleSyllabus = { id, f -> viewModel.toggleSyllabus(id, f) },
                                onAddSyllabusChapter = { viewModel.addSyllabusChapter(it) },
                                onDeleteSyllabus = { viewModel.deleteSyllabus(it) },
                                onToggleAiRevisionDone = { viewModel.toggleAiRevisionDone(it) },
                                onAddAiRevision = { viewModel.addAiRevision(it) }
                            )

                            "matrix" -> EisenhowerMatrixScreen(
                                state = state,
                                selectedQuadrantFilter = matrixFilter,
                                onQuadrantFilterChange = { viewModel.setMatrixQuadrantFilter(it) },
                                onAddTask = { title, prio, lev, eng, quad -> viewModel.addTask(title, prio, lev, eng, quad) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onMoveQuadrant = { id, quad -> viewModel.moveTaskQuadrant(id, quad) },
                                onOpenMorningCheckIn = { viewModel.openPrimeModal("morning") }
                            )

                            "future_goals" -> FutureGoalsScreen(
                                state = state,
                                onOpenAddGoal = { viewModel.openAddGoalModal() },
                                onToggleMilestone = { viewModel.toggleGoalMilestone(it) },
                                onUpdateProgress = { id, p -> viewModel.updateGoalProgress(id, p) },
                                onDeleteGoal = { viewModel.deleteFutureGoal(it) }
                            )

                            "focus" -> TodayScreen(
                                state = state,
                                timerMode = timerMode,
                                timerSeconds = timerSeconds,
                                isTimerRunning = isTimerRunning,
                                energyFilter = energyFilter,
                                onTimerModeChange = { viewModel.setTimerMode(it) },
                                onToggleTimer = { viewModel.toggleTimer() },
                                onResetTimer = { viewModel.resetTimer() },
                                onLogWastedClick = { viewModel.openWastedTimeModal() },
                                onSaveFeynmanGap = { viewModel.saveFeynmanGap(it) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                onAddTask = { title, prio, lev, eng -> viewModel.addTask(title, prio, lev, eng) },
                                onAutoReschedule = { viewModel.autoRescheduleBacklog() },
                                onToggleHabit = { viewModel.toggleHabit(it) },
                                onCompleteSpaced = { viewModel.completeSpacedItem(it) },
                                onOpenAddSpaced = { showAddSpacedModal = true },
                                onOpenAddHabit = { showAddHabitModal = true },
                                onOpenPrimeModal = { viewModel.openPrimeModal(it) }
                            )

                            "pillars" -> PillarsScreen(
                                state = state,
                                pillarSubTab = pillarSubTab,
                                onSubTabChange = { viewModel.setPillarSubTab(it) },
                                onAddVaultItem = { viewModel.addVaultItem(it) },
                                onLogExpense = { amt, cat -> viewModel.logExpense(amt, cat) },
                                onAddBizIdea = { viewModel.addBusinessIdea(it) },
                                onAddDecision = { title, outcome -> viewModel.addDecision(title, outcome) },
                                onUpdateSubjectProgress = { name, done -> viewModel.updateSubjectProgress(name, done) }
                            )

                            else -> SimpleHomeScreen(
                                state = state,
                                onNavigateTab = { viewModel.setMobileTab(it) },
                                onToggleTask = { viewModel.toggleTask(it) },
                                onSprintTrigger = { viewModel.trigger2MinuteSprint() },
                                onOpenPrimeModal = { viewModel.openPrimeModal(it) },
                                onSendNotificationTest = {
                                    viewModel.sendStudyNotification(
                                        "🎯 LifeOS Priority Alert",
                                        "Top 1% Goal Active: Stay consistent on your daily study & high-leverage work!"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diagnostic 20-Q Quiz Dialog
    if (showDiagnostic) {
        DiagnosticQuizDialog(
            questions = state.questions,
            onDismiss = { viewModel.closeDiagnosticModal() },
            onComplete = { scores, tone -> viewModel.completeDiagnosticQuiz(scores, tone) }
        )
    }

    // Add Future Goal Dialog with Ideal Benchmark
    if (showAddGoal) {
        AddFutureGoalDialog(
            onDismiss = { viewModel.closeAddGoalModal() },
            onSave = { title, category, horizon, targetDate, curMetric, idealMetric, curTraj, idealTraj, gap, bridge ->
                viewModel.addFutureGoal(title, category, horizon, targetDate, curMetric, idealMetric, curTraj, idealTraj, gap, bridge)
            }
        )
    }

    // Morning Check-In / Night Brain Dump Dialog
    showPrimeType?.let { primeType ->
        if (primeType == "morning") {
            MorningCheckInDialog(
                initialGoal = state.profile.morningPrime,
                onDismiss = { viewModel.closePrimeModal() },
                onConfirm = { goal, quadrant, energy, leverage ->
                    viewModel.saveMorningCheckIn(goal, quadrant, energy, leverage)
                }
            )
        } else {
            PrimeModalDialog(
                type = primeType,
                initialValue = state.profile.nightDump,
                onDismiss = { viewModel.closePrimeModal() },
                onSave = { viewModel.saveNightDump(it) }
            )
        }
    }

    // Log Wasted Time Dialog
    if (showWastedModal) {
        LogWastedTimeDialog(
            onDismiss = { viewModel.closeWastedTimeModal() },
            onSave = {
                viewModel.logWastedHours(it)
                viewModel.closeWastedTimeModal()
            }
        )
    }

    // Add Spaced Repetition Topic Dialog
    if (showAddSpacedModal) {
        AddSpacedTopicDialog(
            onDismiss = { showAddSpacedModal = false },
            onSave = {
                viewModel.addSpacedTopic(it)
                showAddSpacedModal = false
            }
        )
    }

    // Add Habit Dialog
    if (showAddHabitModal) {
        AddHabitDialog(
            onDismiss = { showAddHabitModal = false },
            onSave = { name, type, impact ->
                viewModel.addHabit(name, type, impact)
                showAddHabitModal = false
            }
        )
    }

    // Export Database Snapshot Dialog
    exportJsonString?.let { json ->
        ExportJsonDialog(
            jsonText = json,
            onDismiss = { exportJsonString = null }
        )
    }
}
