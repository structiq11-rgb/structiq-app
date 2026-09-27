package com.structiq.app.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.ProjectEntity
import com.structiq.app.core.model.ProjectStatus
import com.structiq.app.data.repository.ProjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ProjectsViewModel(private val projectRepository: ProjectRepository) : ViewModel() {

    val projects: StateFlow<List<ProjectEntity>> = projectRepository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createProject(
        name: String,
        client: String,
        location: String,
        valueUsd: Double,
        durationDays: Int,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                id = "proj_${UUID.randomUUID().toString().take(8)}",
                name = name.ifBlank { "Construction Project Site" },
                clientName = client.ifBlank { "Client Entity" },
                location = location.ifBlank { "Site Location" },
                projectValueUsd = valueUsd,
                startDateEpochMs = System.currentTimeMillis(),
                expectedCompletionEpochMs = System.currentTimeMillis() + (durationDays * 24 * 60 * 60 * 1000L),
                status = ProjectStatus.PLANNING,
                progressPercent = 0.05f,
                totalTasksCount = 20,
                completedTasksCount = 1
            )
            projectRepository.createOrUpdateProject(newProject)
            onComplete()
        }
    }
}
