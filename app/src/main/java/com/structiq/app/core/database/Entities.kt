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
