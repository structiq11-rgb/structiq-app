package com.structiq.app.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.structiq.app.core.model.*

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String = "user_default",
    val fullName: String,
    val role: UserRole,
    val companyName: String,
    val email: String,
    val phone: String,
    val isOnboardingCompleted: Boolean = false
)

@Entity(tableName = "tenders")
data class TenderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val clientName: String,
    val tenderNumber: String,
    val closingDateEpochMs: Long,
    val location: String,
    val contractType: ContractType,
    val estimatedValueUsd: Double,
    val status: TenderStatus,
    val notes: String,
    val totalRequirementsCount: Int = 0,
    val completedRequirementsCount: Int = 0
)

@Entity(tableName = "tender_checklist_items")
data class TenderChecklistItemEntity(
    @PrimaryKey val id: String,
    val tenderId: String,
    val category: String, // Administrative, Technical, Financial, Submission
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isRequired: Boolean = true,
    val isCritical: Boolean = false,
    val notes: String = "",
    val supportingDocUri: String? = null,
    val supportingDocName: String? = null
)

@Entity(tableName = "tender_notes")
data class TenderNoteEntity(
    @PrimaryKey val id: String,
    val tenderId: String,
    val noteText: String,
    val category: String = "General Clarification", // Clarification, Internal Review, Question, Submission Instruction
    val createdAtEpochMs: Long
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val clientName: String,
    val location: String,
    val projectValueUsd: Double,
    val startDateEpochMs: Long,
    val expectedCompletionEpochMs: Long,
    val status: ProjectStatus,
    val progressPercent: Float = 0f,
    val totalTasksCount: Int = 0,
    val completedTasksCount: Int = 0
)

@Entity(tableName = "project_tasks")
data class ProjectTaskEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val taskName: String,
    val description: String,
    val assignedPerson: String,
    val startDateEpochMs: Long,
    val dueDateEpochMs: Long,
    val status: String, // Not Started, In Progress, Completed, Delayed
    val progressPercent: Float = 0f, // 0.0 to 1.0
    val priority: String = "Medium", // High, Medium, Low
    val notes: String = ""
)

@Entity(tableName = "site_diary_entries")
data class SiteDiaryEntryEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val entryDateEpochMs: Long,
    val weather: String,
    val sitePersonnelSummary: String,
    val plantEquipmentSummary: String,
    val materialsDelivered: String,
    val workPerformed: String,
    val quantitiesMeasured: String,
    val visitors: String,
    val instructionsReceived: String,
    val delays: String,
    val safetyObservations: String,
    val qualityObservations: String,
    val issues: String,
    val generalNotes: String,
    val photoUrisJson: String = "[]"
)

@Entity(tableName = "project_issues")
data class ProjectIssueEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val description: String,
    val category: String, // Safety, Quality, Programme, Cost, Materials, Design, Client, Subcontractor, Weather, Other
    val priority: String, // High, Medium, Low
    val dateIdentifiedEpochMs: Long,
    val assignedPerson: String,
    val status: String, // Open, In Progress, Resolved, Closed
    val dueDateEpochMs: Long,
    val resolutionNotes: String = "",
    val photoUrisJson: String = "[]"
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: DocumentCategory,
    val fileType: String, // PDF, DOCX, XLSX, PNG
    val sizeBytes: Long,
    val filePath: String,
    val relatedTenderId: String? = null,
    val relatedProjectId: String? = null,
    val createdAtEpochMs: Long
)

@Entity(tableName = "site_reports")
data class SiteReportEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val reportDateEpochMs: Long,
    val weatherCondition: String,
    val temperatureCelsius: Float,
    val labourCount: Int,
    val equipmentSummary: String,
    val activitiesCompleted: String,
    val delaysOrIssues: String,
    val safetyObservations: String,
    val createdBy: String
)

@Entity(tableName = "saved_calculations")
data class CalculationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: CalculationCategory,
    val inputParamsJson: String,
    val formulaUsed: String,
    val resultFormatted: String,
    val timestampEpochMs: Long
)
