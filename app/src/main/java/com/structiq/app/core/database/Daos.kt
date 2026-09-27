package com.structiq.app.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfileFlow(id: String = "user_default"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfile(id: String = "user_default"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}

@Dao
interface TenderDao {
    @Query("SELECT * FROM tenders ORDER BY closingDateEpochMs ASC")
    fun getAllTendersFlow(): Flow<List<TenderEntity>>

    @Query("SELECT * FROM tenders WHERE id = :id")
    suspend fun getTenderById(id: String): TenderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTender(tender: TenderEntity)

    @Update
    suspend fun updateTender(tender: TenderEntity)

    @Delete
    suspend fun deleteTender(tender: TenderEntity)

    @Query("SELECT COUNT(*) FROM tenders")
    fun getTenderCountFlow(): Flow<Int>
}

@Dao
interface TenderChecklistDao {
    @Query("SELECT * FROM tender_checklist_items WHERE tenderId = :tenderId ORDER BY isCritical DESC, category ASC")
    fun getChecklistForTenderFlow(tenderId: String): Flow<List<TenderChecklistItemEntity>>

    @Query("SELECT * FROM tender_checklist_items WHERE tenderId = :tenderId")
    suspend fun getChecklistForTender(tenderId: String): List<TenderChecklistItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: TenderChecklistItemEntity)

    @Update
    suspend fun updateChecklistItem(item: TenderChecklistItemEntity)

    @Delete
    suspend fun deleteChecklistItem(item: TenderChecklistItemEntity)
}

@Dao
interface TenderNoteDao {
    @Query("SELECT * FROM tender_notes WHERE tenderId = :tenderId ORDER BY createdAtEpochMs DESC")
    fun getNotesForTenderFlow(tenderId: String): Flow<List<TenderNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TenderNoteEntity)

    @Delete
    suspend fun deleteNote(note: TenderNoteEntity)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY startDateEpochMs DESC")
    fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface ProjectTaskDao {
    @Query("SELECT * FROM project_tasks WHERE projectId = :projectId ORDER BY dueDateEpochMs ASC")
    fun getTasksForProjectFlow(projectId: String): Flow<List<ProjectTaskEntity>>

    @Query("SELECT * FROM project_tasks WHERE projectId = :projectId")
    suspend fun getTasksForProject(projectId: String): List<ProjectTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ProjectTaskEntity)

    @Update
    suspend fun updateTask(task: ProjectTaskEntity)

    @Delete
    suspend fun deleteTask(task: ProjectTaskEntity)
}

@Dao
interface SiteDiaryEntryDao {
    @Query("SELECT * FROM site_diary_entries WHERE projectId = :projectId ORDER BY entryDateEpochMs DESC")
    fun getDiaryEntriesForProjectFlow(projectId: String): Flow<List<SiteDiaryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: SiteDiaryEntryEntity)

    @Update
    suspend fun updateDiaryEntry(entry: SiteDiaryEntryEntity)

    @Delete
    suspend fun deleteDiaryEntry(entry: SiteDiaryEntryEntity)
}

@Dao
interface ProjectIssueDao {
    @Query("SELECT * FROM project_issues WHERE projectId = :projectId ORDER BY dateIdentifiedEpochMs DESC")
    fun getIssuesForProjectFlow(projectId: String): Flow<List<ProjectIssueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: ProjectIssueEntity)

    @Update
    suspend fun updateIssue(issue: ProjectIssueEntity)

    @Delete
    suspend fun deleteIssue(issue: ProjectIssueEntity)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAtEpochMs DESC")
    fun getAllDocumentsFlow(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE relatedTenderId = :tenderId ORDER BY createdAtEpochMs DESC")
    fun getDocumentsForTenderFlow(tenderId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE relatedProjectId = :projectId ORDER BY createdAtEpochMs DESC")
    fun getDocumentsForProjectFlow(projectId: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: DocumentEntity)
}

@Dao
interface SiteReportDao {
    @Query("SELECT * FROM site_reports WHERE projectId = :projectId ORDER BY reportDateEpochMs DESC")
    fun getReportsForProjectFlow(projectId: String): Flow<List<SiteReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SiteReportEntity)
}

@Dao
interface CalculationDao {
    @Query("SELECT * FROM saved_calculations ORDER BY timestampEpochMs DESC")
    fun getAllCalculationsFlow(): Flow<List<CalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calc: CalculationEntity)
}
