package com.structiq.app.feature.tenders

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.DocumentEntity
import com.structiq.app.core.database.TenderChecklistItemEntity
import com.structiq.app.core.database.TenderEntity
import com.structiq.app.core.database.TenderNoteEntity
import com.structiq.app.core.model.DocumentCategory
import com.structiq.app.core.model.TenderStatus
import com.structiq.app.data.repository.DocumentRepository
import com.structiq.app.data.repository.TenderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class DeadlineStatus {
    GREEN,   // > 7 days remaining
    AMBER,   // 1 to 7 days remaining
    RED,     // < 24 hours remaining
    EXPIRED  // Passed
}

data class BidReadinessSummary(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val outstandingCount: Int = 0,
    val criticalOutstandingCount: Int = 0,
    val countWithDocs: Int = 0,
    val countWithoutDocs: Int = 0,
    val readinessPercentage: Int = 0
)

class TenderDetailViewModel(
    private val tenderId: String,
    private val tenderRepository: TenderRepository,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    val tender: StateFlow<TenderEntity?> = tenderRepository.getAllTenders()
        .map { list -> list.find { it.id == tenderId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val checklistItems: StateFlow<List<TenderChecklistItemEntity>> =
        tenderRepository.getChecklistForTender(tenderId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenderNotes: StateFlow<List<TenderNoteEntity>> =
        tenderRepository.getNotesForTender(tenderId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tenderDocuments: StateFlow<List<DocumentEntity>> =
        documentRepository.getDocumentsForTender(tenderId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readinessSummary: StateFlow<BidReadinessSummary> = checklistItems.map { items ->
        val total = items.size
        val completed = items.count { it.isCompleted }
        val outstanding = total - completed
        val criticalOutstanding = items.count { !it.isCompleted && it.isCritical }
        val withDocs = items.count { !it.supportingDocUri.isNullOrBlank() || !it.supportingDocName.isNullOrBlank() }
        val withoutDocs = total - withDocs
        val pct = if (total > 0) (completed.toFloat() / total.toFloat() * 100).toInt() else 0

        BidReadinessSummary(
            totalCount = total,
            completedCount = completed,
            outstandingCount = outstanding,
            criticalOutstandingCount = criticalOutstanding,
            countWithDocs = withDocs,
            countWithoutDocs = withoutDocs,
            readinessPercentage = pct
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BidReadinessSummary())

    fun toggleChecklistItem(item: TenderChecklistItemEntity) {
        viewModelScope.launch {
            val updated = item.copy(isCompleted = !item.isCompleted)
            tenderRepository.updateChecklistItem(updated)
            syncTenderProgress()
        }
    }

    fun attachDocumentToRequirement(item: TenderChecklistItemEntity, uri: Uri, docName: String) {
        viewModelScope.launch {
            val updated = item.copy(
                supportingDocUri = uri.toString(),
                supportingDocName = docName
            )
            tenderRepository.updateChecklistItem(updated)

            // Save document metadata in Room
            val newDoc = DocumentEntity(
                id = "doc_${UUID.randomUUID().toString().take(8)}",
                title = docName,
                category = DocumentCategory.TENDER_DOC,
                fileType = docName.substringAfterLast('.', "PDF").uppercase(),
                sizeBytes = 1500000L,
                filePath = uri.toString(),
                relatedTenderId = tenderId,
                createdAtEpochMs = System.currentTimeMillis()
            )
            documentRepository.saveDocument(newDoc)
        }
    }

    fun addCustomChecklistItem(
        category: String,
        title: String,
        description: String,
        isCritical: Boolean
    ) {
        viewModelScope.launch {
            val newItem = TenderChecklistItemEntity(
                id = "req_${UUID.randomUUID().toString().take(8)}",
                tenderId = tenderId,
                category = category,
                title = title.ifBlank { "Tender Requirement" },
                description = description,
                isCompleted = false,
                isRequired = true,
                isCritical = isCritical
            )
            tenderRepository.saveChecklistItem(newItem)
            syncTenderProgress()
        }
    }

    fun addTenderNote(noteText: String, category: String) {
        if (noteText.isBlank()) return
        viewModelScope.launch {
            val newNote = TenderNoteEntity(
                id = "note_${UUID.randomUUID().toString().take(8)}",
                tenderId = tenderId,
                noteText = noteText,
                category = category,
                createdAtEpochMs = System.currentTimeMillis()
            )
            tenderRepository.saveTenderNote(newNote)
        }
    }

    fun deleteTenderNote(note: TenderNoteEntity) {
        viewModelScope.launch {
            tenderRepository.deleteTenderNote(note)
        }
    }

    fun updateTenderStatus(status: TenderStatus) {
        viewModelScope.launch {
            tender.value?.let { current ->
                tenderRepository.createOrUpdateTender(current.copy(status = status))
            }
        }
    }

    private suspend fun syncTenderProgress() {
        tender.value?.let { current ->
            val list = checklistItems.value
            val total = list.size
            val completed = list.count { it.isCompleted }
            tenderRepository.createOrUpdateTender(
                current.copy(
                    totalRequirementsCount = total,
                    completedRequirementsCount = completed
                )
            )
        }
    }

    fun calculateDeadlineStatus(closingEpochMs: Long): Pair<DeadlineStatus, String> {
        val now = System.currentTimeMillis()
        val diffMs = closingEpochMs - now

        if (diffMs <= 0) {
            return Pair(DeadlineStatus.EXPIRED, "Expired / Passed")
        }

        val diffHours = diffMs / (1000 * 60 * 60)
        val diffDays = diffHours / 24

        return when {
            diffHours < 24 -> Pair(DeadlineStatus.RED, "$diffHours hours remaining (URGENT)")
            diffDays <= 7 -> Pair(DeadlineStatus.AMBER, "$diffDays days remaining (APPROACHING)")
            else -> Pair(DeadlineStatus.GREEN, "$diffDays days remaining")
        }
    }
}
