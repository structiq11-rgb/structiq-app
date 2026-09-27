package com.structiq.app.feature.tenders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.structiq.app.core.database.TenderEntity
import com.structiq.app.core.model.TenderStatus
import com.structiq.app.data.repository.TenderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TenderRequirementItem(
    val id: String,
    val category: String, // Administrative, Technical, Financial, Submission
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val attachedDocName: String? = null
)

class TenderDetailViewModel(
    private val tenderId: String,
    private val tenderRepository: TenderRepository
) : ViewModel() {

    private val _tender = MutableStateFlow<TenderEntity?>(null)
    val tender: StateFlow<TenderEntity?> = _tender.asStateFlow()

    private val _requirements = MutableStateFlow<List<TenderRequirementItem>>(emptyList())
    val requirements: StateFlow<List<TenderRequirementItem>> = _requirements.asStateFlow()

    init {
        loadTenderDetails()
        loadDefaultChecklist()
    }

    private fun loadTenderDetails() {
        viewModelScope.launch {
            _tender.value = tenderRepository.getTender(tenderId)
        }
    }

    private fun loadDefaultChecklist() {
        _requirements.value = listOf(
            // Administrative
            TenderRequirementItem("req_1", "Administrative", "Certificate of Incorporation / Registration", "Certified copy of company registration certificate", true, "Apex_Registration_2026.pdf"),
            TenderRequirementItem("req_2", "Administrative", "Valid Tax Compliance Certificate", "KRA / Tax Authority valid clearance certificate", true, "Tax_Compliance_2026.pdf"),
            TenderRequirementItem("req_3", "Administrative", "CR12 / Official Directors List", "Search list of directors issued within last 6 months", true, "CR12_Directors.pdf"),
            TenderRequirementItem("req_4", "Administrative", "Single Business Permit", "Valid county / municipal business operation license", false),

            // Technical
            TenderRequirementItem("req_5", "Technical", "Company Technical Profile", "Detailed company profile outlining civil engineering experience", true, "Apex_Corporate_Profile_2026.pdf"),
            TenderRequirementItem("req_6", "Technical", "Method Statement & Sequence", "Detailed methodology for pipe laying / structural works", true, "Method_Statement_Ductile_Iron.docx"),
            TenderRequirementItem("req_7", "Technical", "Key Personnel CVs & Practicing Licenses", "Resident Engineer, Site Agent & Safety Officer CVs", false),
            TenderRequirementItem("req_8", "Technical", "Equipment Ownership / Lease Agreements", "Excavators, batching plant, dump trucks logbooks", false),

            // Financial
            TenderRequirementItem("req_9", "Financial", "Audited Financial Statements (Last 3 Years)", "Signed balance sheets & profit/loss statements", true, "Audited_Accounts_2023_2025.pdf"),
            TenderRequirementItem("req_10", "Financial", "Bid Bond / Tender Security Guarantee", "Original bank guarantee from reputable commercial bank", false),
            TenderRequirementItem("req_11", "Financial", "Priced Bill of Quantities (BOQ)", "Duly filled, stamped & signed pricing schedule", true, "BOQ_Water_Supply_Works.xlsx"),

            // Submission
            TenderRequirementItem("req_12", "Submission", "Form of Tender Signed & Stamped", "Official tender form with total contract bid amount", false)
        )
        updateComplianceScore()
    }

    fun toggleRequirement(id: String) {
        _requirements.value = _requirements.value.map { item ->
            if (item.id == id) item.copy(isCompleted = !item.isCompleted) else item
        }
        updateComplianceScore()
    }

    fun updateStatus(newStatus: TenderStatus) {
        viewModelScope.launch {
            _tender.value?.let { current ->
                val updated = current.copy(status = newStatus)
                tenderRepository.createOrUpdateTender(updated)
                _tender.value = updated
            }
        }
    }

    private fun updateComplianceScore() {
        val completed = _requirements.value.count { it.isCompleted }
        val total = _requirements.value.size
        viewModelScope.launch {
            _tender.value?.let { current ->
                val updated = current.copy(
                    totalRequirementsCount = total,
                    completedRequirementsCount = completed
                )
                tenderRepository.createOrUpdateTender(updated)
                _tender.value = updated
            }
        }
    }
}
