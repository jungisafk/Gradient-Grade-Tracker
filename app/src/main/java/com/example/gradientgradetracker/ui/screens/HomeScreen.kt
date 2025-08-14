package com.example.gradientgradetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.gradientgradetracker.data.model.AssessmentEntry
import com.example.gradientgradetracker.data.model.SubjectOverview
import com.example.gradientgradetracker.data.model.SubjectStatus
import com.example.gradientgradetracker.data.model.computePeriodGrade
import com.example.gradientgradetracker.data.model.Subject
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.gradientgradetracker.data.datastore.UiState
import com.example.gradientgradetracker.data.datastore.UiStateStore
import androidx.room.Room
import com.example.gradientgradetracker.data.local.AppDb
import com.example.gradientgradetracker.data.local.AssessmentEntity
import com.example.gradientgradetracker.data.local.SubjectEntity
import com.example.gradientgradetracker.data.repository.GradesRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.example.gradientgradetracker.work.SyncWorker
import kotlinx.coroutines.launch
import java.util.UUID
import java.io.File

@Composable
fun HomeScreen(
    userName: String = "Student",
    currentPeriod: String = "Midterms",
    week: Int = 8,
    totalWeeks: Int = 18,
    gwa: Double = 2.85,
    subjectsAtRisk: Int = 2,
    passingRate: Int = 71,
    targetProgress: Int = 85,
    prelim: Double = 2.1,
    midterm: Double = 1.5,
    final: Double = 1.8,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    // DataStore for UI state persistence
    val dataStore: DataStore<Preferences> = remember {
        PreferenceDataStoreFactory.create(
            produceFile = { File(context.filesDir, "ui_state_prefs.preferences_pb") }
        )
    }
    val uiStateStore = remember { UiStateStore(dataStore) }
    val uiState by uiStateStore.uiFlow.collectAsState(initial = UiState(0, "Home", "[]", "{}"))

    var selectedNavIndex by rememberSaveable { mutableStateOf(uiState.selectedTab) }
    LaunchedEffect(uiState.selectedTab) { selectedNavIndex = uiState.selectedTab }
    LaunchedEffect(selectedNavIndex) { uiStateStore.setSelectedTab(selectedNavIndex) }

    // Local Room DB + repository
    val appDb = remember {
        Room.databaseBuilder(context, AppDb::class.java, "grades.db")
            .addMigrations(com.example.gradientgradetracker.data.local.DbMigrations.MIGRATION_1_2)
            .build()
    }
    val repository = remember { GradesRepository(appDb, FirebaseFirestore.getInstance()) }
    val navItems = listOf(
        "Home" to Icons.Default.Home,
        "Grades" to Icons.Default.DateRange,
        "Reminders" to Icons.Default.Notifications,
        "Settings" to Icons.Default.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF185A9D),
                contentColor = Color.White
            ) {
                navItems.forEachIndexed { index, pair ->
                    NavigationBarItem(
                        selected = selectedNavIndex == index,
                        onClick = { selectedNavIndex = index },
                        icon = {
                            Icon(
                                imageVector = pair.second,
                                contentDescription = pair.first,
                                tint = if (selectedNavIndex == index) Color(0xFFFFC107) else Color.White
                            )
                        },
                        label = {
                            Text(
                                pair.first,
                                color = if (selectedNavIndex == index) Color(0xFFFFC107) else Color.White
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Top Bar
            val (title, subtitle) = when (selectedNavIndex) {
                0 -> "Academic Dashboard" to "Current Period: $currentPeriod - Week $week of $totalWeeks"
                1 -> "Grades" to "View and manage your grades"
                2 -> "Reminders" to "Stay on top of important dates"
                3 -> "Settings" to "Customize your experience"
                else -> "" to ""
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF185A9D))
                    .padding(top = 32.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            // Content
            var subjects by remember { mutableStateOf(listOf<SubjectUI>()) }

            // Enqueue background sync on start
            LaunchedEffect(Unit) { SyncWorker.enqueue(context) }

            val scope = rememberCoroutineScope()
            // Observe local Room subjects and assessments
            LaunchedEffect(repository) {
                repository.subjectsFlow.collect { entities ->
                    // Base subjects
                    subjects = entities.map { SubjectUI(subject = Subject(id = it.id, name = it.name), icon = it.icon) }
                    // For each, collect assessments
                    entities.forEach { e ->
                        scope.launch {
                            repository.assessmentsFlow(e.id).collect { items ->
                                val grouped = items.groupBy { it.period }.mapValues { (_, list) ->
                                    list.map { ae ->
                                        AssessmentEntry(
                                            name = ae.type,
                                            scoreObtained = ae.score,
                                            totalScore = ae.total,
                                            weight = ae.weight,
                                            date = ae.date,
                                            assessmentType = ae.type
                                        )
                                    }.toMutableList()
                                }
                                val defaultMap = mutableMapOf(
                                    "Prelim" to mutableListOf<AssessmentEntry>(),
                                    "Midterm" to mutableListOf<AssessmentEntry>(),
                                    "Final" to mutableListOf<AssessmentEntry>()
                                )
                                grouped.forEach { (k, v) -> defaultMap[k] = v }
                                subjects = subjects.map { s -> if (s.subject.id == e.id) s.copy(assessments = defaultMap) else s }
                            }
                        }
                    }
                }
            }

            // Start remote snapshot listeners (cross-device sync)
            LaunchedEffect(Unit) {
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    repository.startSubjectsListener(userId)
                    repository.startAssessmentsListener(userId)
                }
            }

            // Handlers
            fun addSubject(subject: Subject, icon: String) {
                val now = System.currentTimeMillis()
                val id = if (subject.id.isBlank()) UUID.randomUUID().toString() else subject.id
                val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                val entity = SubjectEntity(id = id, name = subject.name, icon = icon, updatedAt = now, userId = uid, pendingOp = "insert")
                scope.launch {
                    repository.upsertSubject(entity)
                    // Ensure a sync is scheduled so subjects appear in Firestore
                    SyncWorker.enqueue(context)
                }
            }

            fun removeSubject(id: String) {
                subjects = subjects.filterNot { it.subject.id == id }
            }

            fun addAssessment(subjectId: String, period: String, entry: AssessmentEntry) {
                // Optimistic UI
                subjects = subjects.map { subj ->
                    if (subj.subject.id == subjectId) {
                        subj.copy(assessments = subj.assessments.toMutableMap().apply {
                            getOrPut(period) { mutableListOf() }.add(entry)
                        })
                    } else subj
                }
                val entity = AssessmentEntity(
                    localId = UUID.randomUUID().toString(),
                    remoteId = null,
                    subjectId = subjectId,
                    period = period,
                    type = entry.assessmentType,
                    score = entry.scoreObtained,
                    total = entry.totalScore,
                    weight = entry.weight,
                    date = entry.date,
                    updatedAt = System.currentTimeMillis(),
                    pendingOp = "insert"
                )
                scope.launch {
                    repository.addOrUpdateAssessment(entity)
                    SyncWorker.enqueue(context)
                }
            }

            fun removeAssessment(subjectId: String, period: String, index: Int) {
                subjects = subjects.map { subj ->
                    if (subj.subject.id == subjectId) {
                        subj.copy(assessments = subj.assessments.toMutableMap().apply {
                            getOrPut(period) { mutableListOf() }.let {
                                if (index in it.indices) it.removeAt(index)
                            }
                        })
                    } else subj
                }
            }

            // --- Pass state and handlers to tabs ---
            when (selectedNavIndex) {
                0 -> OverviewTab(subjects.map {
                    SubjectOverview(
                        id = it.subject.id,
                        name = it.subject.name,
                        currentGrade = computePeriodGrade(it.assessments["Final"] ?: emptyList()),
                        prelim = computePeriodGrade(it.assessments["Prelim"] ?: emptyList()),
                        midterm = computePeriodGrade(it.assessments["Midterm"] ?: emptyList()),
                        final = computePeriodGrade(it.assessments["Final"] ?: emptyList()),
                        status = if (computePeriodGrade(it.assessments["Final"] ?: emptyList()) < 3.0) SubjectStatus.ALERT else SubjectStatus.ON_TRACK,
                        assessments = it.assessments
                    )
                })
                1 -> SubjectsTab(
                    subjects = subjects,
                    onAddSubject = ::addSubject,
                    onRemoveSubject = ::removeSubject,
                    onAddAssessment = ::addAssessment,
                    onRemoveAssessment = ::removeAssessment
                )
                2 -> AnalyticsTab(
                    subjects = subjects.map {
                        SubjectOverview(
                            id = it.subject.id,
                            name = it.subject.name,
                            currentGrade = computePeriodGrade(it.assessments["Final"] ?: emptyList()),
                            prelim = computePeriodGrade(it.assessments["Prelim"] ?: emptyList()),
                            midterm = computePeriodGrade(it.assessments["Midterm"] ?: emptyList()),
                            final = computePeriodGrade(it.assessments["Final"] ?: emptyList()),
                            status = if (computePeriodGrade(it.assessments["Final"] ?: emptyList()) < 3.0) SubjectStatus.ALERT else SubjectStatus.ON_TRACK,
                            assessments = it.assessments
                        )
                    },
                    assessmentsByPeriod = mapOf(
                        "Prelim" to subjects.flatMap { it.assessments["Prelim"] ?: emptyList() },
                        "Midterm" to subjects.flatMap { it.assessments["Midterm"] ?: emptyList() },
                        "Final" to subjects.flatMap { it.assessments["Final"] ?: emptyList() }
                    )
                )
                3 -> SettingsTab(onLoggedOut = { onLogout() })
            }
        }
    }
}