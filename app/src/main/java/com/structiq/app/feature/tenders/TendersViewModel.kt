package com.structiq.app.feature.tenders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.TenderEntity
import com.structiq.app.core.model.ContractType
import com.structiq.app.core.model.TenderStatus
import com.structiq.app.data.repository.TenderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TendersViewModel(private val tenderRepository: TenderRepository) : ViewModel() {

    val tenders: StateFlow<List<TenderEntity>> = tenderRepository.getAllTenders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedStatusFilter = MutableStateFlow<TenderStatus?>(null)
    val selectedStatusFilter: StateFlow<TenderStatus?> = _selectedStatusFilter.asStateFlow()

    fun filterByStatus(status: TenderStatus?) {
        _selectedStatusFilter.value = status
    }

    fun createTender(
        title: String,
        client: String,
        tenderNum: String,
        location: String,
        contractType: ContractType,
        estValueUsd: Double,
        closingDaysFromNow: Int,
        notes: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val newTender = TenderEntity(
                id = "tender_${UUID.randomUUID().toString().take(8)}",
                title = title.ifBlank { "Untitled Tender Workspace" },
                clientName = client.ifBlank { "Client Entity" },
                tenderNumber = tenderNum.ifBlank { "TND/2026/001" },
                closingDateEpochMs = System.currentTimeMillis() + (closingDaysFromNow * 24 * 60 * 60 * 1000L),
                location = location.ifBlank { "Site Location" },
                contractType = contractType,
                estimatedValueUsd = estValueUsd,
                status = TenderStatus.PREPARING,
                notes = notes,
                totalRequirementsCount = 12,
                completedRequirementsCount = 0
            )
            tenderRepository.createOrUpdateTender(newTender)
            onComplete()
        }
    }
}
