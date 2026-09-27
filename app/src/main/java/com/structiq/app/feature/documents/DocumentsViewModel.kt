package com.structiq.app.feature.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.DocumentEntity
import com.structiq.app.core.model.DocumentCategory
import com.structiq.app.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class DocumentsViewModel(private val documentRepository: DocumentRepository) : ViewModel() {

    val documents: StateFlow<List<DocumentEntity>> = documentRepository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<DocumentCategory?>(null)
    val selectedCategory: StateFlow<DocumentCategory?> = _selectedCategory.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(cat: DocumentCategory?) {
        _selectedCategory.value = cat
    }

    fun uploadDocument(
        title: String,
        category: DocumentCategory,
        fileType: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val newDoc = DocumentEntity(
                id = "doc_${UUID.randomUUID().toString().take(8)}",
                title = title.ifBlank { "Untitled Document.pdf" },
                category = category,
                fileType = fileType,
                sizeBytes = (1000000..5000000).random().toLong(),
                filePath = "/documents/$title",
                createdAtEpochMs = System.currentTimeMillis()
            )
            documentRepository.saveDocument(newDoc)
            onComplete()
        }
    }
}
