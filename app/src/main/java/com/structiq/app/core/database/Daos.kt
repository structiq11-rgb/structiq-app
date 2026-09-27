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
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdAtEpochMs DESC")
    fun getAllDocumentsFlow(): Flow<List<DocumentEntity>>

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
