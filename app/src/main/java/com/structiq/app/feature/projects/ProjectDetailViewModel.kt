package com.structiq.app.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.ProjectEntity
import com.structiq.app.core.database.SiteReportEntity
import com.structiq.app.data.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class ProjectTaskItem(
    val id: String,
    val title: String,
    val assignee: String,
    val priority: String, // High, Medium, Low
    val isCompleted: Boolean = false
)

data class ProjectIssueItem(
    val id: String,
    val title: String,
    val severity: String, // Critical, Major, Minor
    val status: String, // Open, Resolving, Closed
    val dateIdentified: String
)

class ProjectDetailViewModel(
    private val projectId: String,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _project = MutableStateFlow<ProjectEntity?>(null)
    val project: StateFlow<ProjectEntity?> = _project.asStateFlow()

    private val _tasks = MutableStateFlow<List<ProjectTaskItem>>(emptyList())
    val tasks: StateFlow<List<ProjectTaskItem>> = _tasks.asStateFlow()

    private val _siteReports = MutableStateFlow<List<SiteReportEntity>>(emptyList())
    val siteReports: StateFlow<List<SiteReportEntity>> = _siteReports.asStateFlow()

    private val _issues = MutableStateFlow<List<ProjectIssueItem>>(emptyList())
    val issues: StateFlow<List<ProjectIssueItem>> = _issues.asStateFlow()

    init {
        loadProjectDetails()
        loadSampleTasksAndDiary()
    }

    private fun loadProjectDetails() {
        viewModelScope.launch {
            _project.value = projectRepository.getProject(projectId)
        }
    }

    private fun loadSampleTasksAndDiary() {
        _tasks.value = listOf(
            ProjectTaskItem("task_1", "Excavation for Main Sewerage Treatment Tank A", "Eng. John Otieno", "High", true),
            ProjectTaskItem("task_2", "Pouring C30 Structural Concrete Foundation Slab", "Foreman James", "High", true),
            ProjectTaskItem("task_3", "Curing Foundation & Slump Test Verification", "Lab Tech Mercy", "Medium", false),
            ProjectTaskItem("task_4", "Delivery of 450mm Ductile Iron Pipes", "Logistics Team", "Medium", false)
        )

        _siteReports.value = listOf(
            SiteReportEntity(
                id = "report_1",
                projectId = projectId,
                reportDateEpochMs = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L),
                weatherCondition = "Clear & Sunny (28°C)",
                temperatureCelsius = 28.0f,
                labourCount = 24,
                equipmentSummary = "2x Excavators 20T, 1x Batching Plant, 2x Plate Compactors",
                activitiesCompleted = "Poured 45m³ of C30 concrete for Tank A foundation slab. Slump test 75mm verified.",
                delaysOrIssues = "30-min delay in ready-mix truck delivery due to city traffic.",
                safetyObservations = "All site workers equipped with hard hats, safety boots & reflective vests. Zero incidents reported.",
                createdBy = "Eng. David Mwangi"
            )
        )

        _issues.value = listOf(
            ProjectIssueItem("issue_1", "Minor honeycombing on column C4 pour", "Minor", "Resolving", "2026-09-25"),
            ProjectIssueItem("issue_2", "Water ingress in West trench after heavy morning rain", "Major", "Open", "2026-09-26")
        )
    }

    fun toggleTask(taskId: String) {
        _tasks.value = _tasks.value.map { t ->
            if (t.id == taskId) t.copy(isCompleted = !t.isCompleted) else t
        }
    }

    fun addSiteReport(
        weather: String,
        labourCount: Int,
        equipment: String,
        activities: String,
        safety: String
    ) {
        val newReport = SiteReportEntity(
            id = "report_${UUID.randomUUID().toString().take(8)}",
            projectId = projectId,
            reportDateEpochMs = System.currentTimeMillis(),
            weatherCondition = weather,
            temperatureCelsius = 26.0f,
            labourCount = labourCount,
            equipmentSummary = equipment,
            activitiesCompleted = activities,
            delaysOrIssues = "None",
            safetyObservations = safety,
            createdBy = "Eng. Site Agent"
        )
        _siteReports.value = listOf(newReport) + _siteReports.value
    }
}
