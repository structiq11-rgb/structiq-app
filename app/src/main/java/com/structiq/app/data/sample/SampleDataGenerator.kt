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
            totalRequirementsCount = 12,
            completedRequirementsCount = 7
        ),
        TenderEntity(
            id = "tender_102",
            title = "A104 Dual Carriageway Overpass Bridge Rehabilitation & Culverts",
            clientName = "National Highways Authority",
            tenderNumber = "NHA/HWY/2026/042",
            closingDateEpochMs = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L), // 3 days left (AMBER)
            location = "Nakuru Corridor",
            contractType = ContractType.FIDIC_RED,
            estimatedValueUsd = 2800000.0,
            status = TenderStatus.REVIEWING,
            notes = "Requires structural concrete C35/45, post-tensioned girders, traffic diversion plan and EIA audit.",
            totalRequirementsCount = 10,
            completedRequirementsCount = 4
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
            totalRequirementsCount = 14,
            completedRequirementsCount = 2
        )
    )

    val sampleChecklistItems = listOf(
        // Administrative
        TenderChecklistItemEntity("req_1", "tender_101", "Administrative", "Certificate of Incorporation / Registration", "Certified copy of company registration certificate", true, isRequired = true, isCritical = true, supportingDocName = "Apex_Registration_2026.pdf"),
        TenderChecklistItemEntity("req_2", "tender_101", "Administrative", "Valid Tax Compliance Certificate", "KRA / Tax Authority valid clearance certificate", true, isRequired = true, isCritical = true, supportingDocName = "Tax_Compliance_2026.pdf"),
        TenderChecklistItemEntity("req_3", "tender_101", "Administrative", "CR12 / Official Directors List", "Search list of directors issued within last 6 months", true, isRequired = true, isCritical = false, supportingDocName = "CR12_Directors.pdf"),
        TenderChecklistItemEntity("req_4", "tender_101", "Administrative", "Single Business Permit", "Valid county / municipal business operation license", false, isRequired = true, isCritical = false),

        // Technical
        TenderChecklistItemEntity("req_5", "tender_101", "Technical", "Company Technical Profile", "Detailed company profile outlining civil engineering experience", true, isRequired = true, isCritical = false, supportingDocName = "Apex_Corporate_Profile_2026.pdf"),
        TenderChecklistItemEntity("req_6", "tender_101", "Technical", "Method Statement & Work Sequence", "Detailed methodology for pipe laying / structural works", true, isRequired = true, isCritical = true, supportingDocName = "Method_Statement_Ductile_Iron.docx"),
        TenderChecklistItemEntity("req_7", "tender_101", "Technical", "Key Personnel CVs & Practicing Licenses", "Resident Engineer, Site Agent & Safety Officer CVs", false, isRequired = true, isCritical = true),
        TenderChecklistItemEntity("req_8", "tender_101", "Technical", "Equipment Ownership / Lease Agreements", "Excavators, batching plant, dump trucks logbooks", false, isRequired = true, isCritical = false),

        // Financial
        TenderChecklistItemEntity("req_9", "tender_101", "Financial", "Audited Financial Statements (Last 3 Years)", "Signed balance sheets & profit/loss statements", true, isRequired = true, isCritical = true, supportingDocName = "Audited_Accounts_2023_2025.pdf"),
        TenderChecklistItemEntity("req_10", "tender_101", "Financial", "Bid Security / Bank Guarantee ($90,000)", "Original bank guarantee from reputable commercial bank", false, isRequired = true, isCritical = true),
        TenderChecklistItemEntity("req_11", "tender_101", "Financial", "Priced Bill of Quantities (BOQ)", "Duly filled, stamped & signed pricing schedule", true, isRequired = true, isCritical = true, supportingDocName = "BOQ_Water_Supply_Works.xlsx"),

        // Submission
        TenderChecklistItemEntity("req_12", "tender_101", "Submission", "Form of Tender Signed & Stamped", "Official tender form with total contract bid amount", false, isRequired = true, isCritical = true)
    )

    val sampleTenderNotes = listOf(
        TenderNoteEntity("note_1", "tender_101", "Clarification #1 received from Ministry of Water: Section 3.02 ductile iron fittings can be Grade K9 or K12.", "Clarification", System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L)),
        TenderNoteEntity("note_2", "tender_101", "Internal review: Ensure Bid Bond of $90,000 is requested from Equity Bank by Tuesday.", "Internal Review", System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L))
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
            totalTasksCount = 4,
            completedTasksCount = 2
        ),
        ProjectEntity(
            id = "proj_02",
            name = "Savannah Heights 12-Story Structural Frame",
            clientName = "Savannah Real Estate Developers",
            location = "Westlands, Nairobi",
            projectValueUsd = 8900000.0,
            startDateEpochMs = System.currentTimeMillis() - (120 * 24 * 60 * 60 * 1000L),
            expectedCompletionEpochMs = System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000L),
            status = ProjectStatus.IN_PROGRESS,
            progressPercent = 0.68f,
            totalTasksCount = 5,
            completedTasksCount = 3
        )
    )

    val sampleTasks = listOf(
        ProjectTaskEntity("task_1", "proj_01", "Excavation for Main Sewerage Treatment Tank A", "Trench excavation & soil compaction to 1.5m depth", "Eng. John Otieno", System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000L), System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L), "Completed", 1.0f, "High", "Verified by Resident Engineer"),
        ProjectTaskEntity("task_2", "proj_01", "Pouring C30 Structural Concrete Foundation Slab", "Pour 45m³ ready-mix C30 concrete with vibrating pokers", "Foreman James", System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L), System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000L), "Completed", 1.0f, "High", "75mm Slump test passed"),
        ProjectTaskEntity("task_3", "proj_01", "Curing Foundation & Slump Test Cube Testing", "7-day & 28-day cube strength tests at lab", "Lab Tech Mercy", System.currentTimeMillis(), System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L), "In Progress", 0.3f, "Medium", "Curing hessian damp sheets placed"),
        ProjectTaskEntity("task_4", "proj_01", "Delivery & Lowering of 450mm Ductile Iron Pipes", "Lowering pipe sections using 20T excavator", "Logistics Team", System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L), System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L), "Not Started", 0.0f, "Medium")
    )

    val sampleDiaryEntries = listOf(
        SiteDiaryEntryEntity(
            id = "diary_1",
            projectId = "proj_01",
            entryDateEpochMs = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L),
            weather = "Clear & Sunny (28°C)",
            sitePersonnelSummary = "1 Site Agent, 1 Quality Eng, 4 Pipe Fitters, 18 Labourers",
            plantEquipmentSummary = "2x Excavators 20T, 1x Batching Plant, 2x Plate Compactors",
            materialsDelivered = "120 Bags Cement (50kg), 40 Tons Sand, 60 Tons Aggregates",
            workPerformed = "Poured 45m³ of C30 concrete for Tank A foundation slab.",
            quantitiesMeasured = "45 m³ Concrete poured, 120m Granular bedding placed",
            visitors = "Eng. Kamau (Superintending Engineer)",
            instructionsReceived = "Ensure 28-day curing period maintained before wall shuttering.",
            delays = "30-min delay in ready-mix truck delivery due to traffic.",
            safetyObservations = "100% PPE compliance. Zero site accidents.",
            qualityObservations = "Concrete slump measured at 75mm (within 70-90mm spec).",
            issues = "Minor honeycombing on column C4 base.",
            generalNotes = "Site progress on schedule."
        )
    )

    val sampleIssues = listOf(
        ProjectIssueEntity("issue_1", "proj_01", "Minor honeycombing on column C4 foundation pour", "Superficial voiding observed after formwork stripping.", "Quality", "Minor", System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L), "Eng. John Otieno", "In Progress", System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L), "Apply high-strength non-shrink grout (Sika Grout 214) under RE supervision."),
        ProjectIssueEntity("issue_2", "proj_01", "Water ingress in West trench after heavy morning rain", "Water accumulation at trench section 2+400 requiring dewatering pumps.", "Weather", "Major", System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000L), "Foreman James", "Open", System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000L))
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
