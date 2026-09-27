package com.structiq.app.data.repository

import com.structiq.app.core.database.*
import com.structiq.app.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TenderRepository(private val tenderDao: TenderDao) {
    fun getAllTenders(): Flow<List<TenderEntity>> = tenderDao.getAllTendersFlow()
    suspend fun getTender(id: String): TenderEntity? = tenderDao.getTenderById(id)
    suspend fun createOrUpdateTender(tender: TenderEntity) = tenderDao.insertTender(tender)
    suspend fun deleteTender(tender: TenderEntity) = tenderDao.deleteTender(tender)
}

class ProjectRepository(private val projectDao: ProjectDao) {
    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjectsFlow()
    suspend fun getProject(id: String): ProjectEntity? = projectDao.getProjectById(id)
    suspend fun createOrUpdateProject(project: ProjectEntity) = projectDao.insertProject(project)
    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)
}

class DocumentRepository(private val documentDao: DocumentDao) {
    fun getAllDocuments(): Flow<List<DocumentEntity>> = documentDao.getAllDocumentsFlow()
    suspend fun saveDocument(doc: DocumentEntity) = documentDao.insertDocument(doc)
    suspend fun deleteDocument(doc: DocumentEntity) = documentDao.deleteDocument(doc)
}

class CalculationRepository(private val calculationDao: CalculationDao) {
    fun getAllSavedCalculations(): Flow<List<CalculationEntity>> = calculationDao.getAllCalculationsFlow()
    suspend fun saveCalculation(calc: CalculationEntity) = calculationDao.insertCalculation(calc)
}

class UserRepository(private val userProfileDao: UserProfileDao) {
    fun getUserProfile(): Flow<UserProfileEntity?> = userProfileDao.getUserProfileFlow()
    suspend fun updateProfile(profile: UserProfileEntity) = userProfileDao.insertOrUpdateProfile(profile)
}

data class AIMessageItem(
    val id: String,
    val sender: String, // "USER" or "AI"
    val messageText: String,
    val timestampMs: Long = System.currentTimeMillis()
)

class AIAssistantRepository {
    fun sendQuery(mode: AIMode, queryText: String): Flow<AIMessageItem> = flow {
        val response = when (mode) {
            AIMode.TENDER_ASSISTANT -> 
                "**Tender Analysis Notice [${mode.title}]**:\n\nBased on standard procurement guidelines for construction tenders:\n1. Ensure Tax Compliance & Certificate of Incorporation are certified.\n2. Review BOQ Item 3.02 for high-risk pricing items.\n3. Include a Site Visit Certificate signed by the Superintending Engineer.\n\n*Note: Please verify specific tender instructions in section ITB 4.2.*"
            
            AIMode.ENGINEERING_ASSISTANT ->
                "**Engineering Guidance [${mode.title}]**:\n\nFor Concrete Mix C25/30 (1:1.5:3 nominal proportion):\n- Cement: ~380 kg/m³ (~7.6 bags)\n- Sand (Zone 2): ~680 kg/m³\n- Coarse Aggregate (20mm): ~1,200 kg/m³\n- Water/Cement Ratio: 0.45 to 0.50\n\n*Warning: Mix design should be verified via laboratory trial mixes according to BS EN 206 / IS 456.*"
            
            AIMode.SITE_ASSISTANT ->
                "**Site Operations Checklist [${mode.title}]**:\n\nDaily Site Diary Outline:\n- Weather: Note morning & afternoon rain delays.\n- Equipment: Check excavator hydraulic pressure & batching plant calibration.\n- Slump Test: Record slump for poured column batch C30."

            else ->
                "**Struct-IQ AI Assistant [${mode.title}]**:\n\nI can assist with method statements, technical proposals, BOQ summaries, and safety risk assessments for your active projects."
        }
        emit(AIMessageItem(id = System.currentTimeMillis().toString(), sender = "AI", messageText = response))
    }
}
