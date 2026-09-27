package com.structiq.app.data.sample

import com.structiq.app.core.database.*
import com.structiq.app.core.model.*

object SampleDataGenerator {
    val sampleUserProfile = UserProfileEntity(
        id = "user_default",
        fullName = "Eng. David Mwangi",
        role = UserRole.CONTRACTOR,
        companyName = "Apex Civil Construction & Infrastructure Ltd",
        email = "david.m@apexcivil.com",
        phone = "+254 712 345 678",
        isOnboardingCompleted = true
    )

    val sampleTenders = listOf(
        TenderEntity(
            id = "tender_101",
            title = "Nairobi Metropolitan Bulk Water Supply & Pipeline Phase 2",
            clientName = "Ministry of Water & Sanitation",
            tenderNumber = "MOWS/TND/2026/089",
            closingDateEpochMs = System.currentTimeMillis() + (12 * 24 * 60 * 60 * 1000L), // 12 days left
            location = "Nairobi County, Kenya",
            contractType = ContractType.MEASURED_BOQ,
            estimatedValueUsd = 4500000.0,
            status = TenderStatus.PREPARING,
            notes = "Key requirements: 450mm Ductile Iron pipe laying, RC storage tank 2,000m³, chlorination plant. Tax compliance & Bid Security $90,000 required.",
            totalRequirementsCount = 18,
            completedRequirementsCount = 14
        ),
        TenderEntity(
            id = "tender_102",
            title = "A104 Dual Carriageway Overpass Bridge Rehabilitation & Culverts",
            clientName = "National Highways Authority",
            tenderNumber = "NHA/HWY/2026/042",
            closingDateEpochMs = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000L), // 5 days left
            location = "Nakuru Corridor",
            contractType = ContractType.FIDIC_RED,
            estimatedValueUsd = 2800000.0,
            status = TenderStatus.REVIEWING,
            notes = "Requires structural concrete C35/45, post-tensioned girders, traffic diversion plan and EIA audit.",
            totalRequirementsCount = 15,
            completedRequirementsCount = 8
        ),
        TenderEntity(
            id = "tender_103",
            title = "Kilifi Commercial Trade Hub & Logistics Park Site Infrastructure",
            clientName = "Coast Development Corporation",
            tenderNumber = "CDC/INF/2026/012",
            closingDateEpochMs = System.currentTimeMillis() + (22 * 24 * 60 * 60 * 1000L),
            location = "Kilifi Port Zone",
            contractType = ContractType.DESIGN_BUILD,
            estimatedValueUsd = 6200000.0,
            status = TenderStatus.DRAFT,
            notes = "Heavy pilling works, soil stabilization, asphalt access roads and stormwater drainage installation.",
            totalRequirementsCount = 20,
            completedRequirementsCount = 4
        )
    )

    val sampleProjects = listOf(
        ProjectEntity(
            id = "proj_01",
            name = "Ruiru Sewerage Treatment Plant Expansion",
            clientName = "County Urban Water Board",
            location = "Ruiru Sub-County",
            projectValueUsd = 3400000.0,
            startDateEpochMs = System.currentTimeMillis() - (60 * 24 * 60 * 60 * 1000L),
            expectedCompletionEpochMs = System.currentTimeMillis() + (180 * 24 * 60 * 60 * 1000L),
            status = ProjectStatus.IN_PROGRESS,
            progressPercent = 0.42f,
            totalTasksCount = 36,
            completedTasksCount = 15
        ),
        ProjectEntity(
            id = "proj_02",
            name = "Savannah Heights 12-Story Structural Reinforced Concrete Frame",
            clientName = "Savannah Real Estate Developers",
            location = "Westlands, Nairobi",
            projectValueUsd = 8900000.0,
            startDateEpochMs = System.currentTimeMillis() - (120 * 24 * 60 * 60 * 1000L),
            expectedCompletionEpochMs = System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000L),
            status = ProjectStatus.IN_PROGRESS,
            progressPercent = 0.68f,
            totalTasksCount = 52,
            completedTasksCount = 35
        )
    )

    val sampleDocuments = listOf(
        DocumentEntity(
            id = "doc_1",
            title = "Apex Civil - Corporate Profile 2026.pdf",
            category = DocumentCategory.COMPANY_DOC,
            fileType = "PDF",
            sizeBytes = 4850000L,
            filePath = "/documents/Apex_Corporate_Profile_2026.pdf",
            createdAtEpochMs = System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000L)
        ),
        DocumentEntity(
            id = "doc_2",
            title = "Method Statement - Ductile Iron Pipe Laying.docx",
            category = DocumentCategory.TENDER_DOC,
            fileType = "DOCX",
            sizeBytes = 1250000L,
            filePath = "/documents/Method_Statement_Ductile_Iron.docx",
            relatedTenderId = "tender_101",
            createdAtEpochMs = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)
        ),
        DocumentEntity(
            id = "doc_3",
            title = "Bill of Quantities - Water Supply Works.xlsx",
            category = DocumentCategory.BOQ,
            fileType = "XLSX",
            sizeBytes = 2300000L,
            filePath = "/documents/BOQ_Water_Supply_Works.xlsx",
            relatedTenderId = "tender_101",
            createdAtEpochMs = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L)
        )
    )

    val sampleCalculations = listOf(
        CalculationEntity(
            id = "calc_1",
            title = "RC Foundation Slab Concrete Volume & Mix",
            category = CalculationCategory.CONCRETE,
            inputParamsJson = "{\"length_m\": 18.5, \"width_m\": 12.0, \"thickness_m\": 0.45, \"mix\": \"1:2:4 (M20)\"}",
            formulaUsed = "V = L × W × T (m³), Dry Volume = V × 1.54",
            resultFormatted = "99.90 m³ Concrete | 647 Bags Cement | 45.4 m³ Sand | 90.8 m³ Aggregate",
            timestampEpochMs = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L)
        ),
        CalculationEntity(
            id = "calc_2",
            title = "Y16 High Yield Steel Rebar Tonnage",
            category = CalculationCategory.REINFORCEMENT,
            inputParamsJson = "{\"diameter_mm\": 16, \"total_length_m\": 1450.0}",
            formulaUsed = "Weight (kg/m) = D² / 162",
            resultFormatted = "2,291.00 kg (2.29 Metric Tons)",
            timestampEpochMs = System.currentTimeMillis() - (4 * 24 * 60 * 60 * 1000L)
        )
    )
}
