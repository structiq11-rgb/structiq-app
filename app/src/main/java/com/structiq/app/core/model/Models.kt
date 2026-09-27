package com.structiq.app.core.model

import androidx.compose.ui.graphics.Color
import com.structiq.app.core.designsystem.theme.*

enum class UserRole(val label: String, val description: String) {
    CONTRACTOR("General / Sub Contractor", "Tender bidding, site works, material & progress management"),
    CIVIL_ENGINEER("Civil / Site Engineer", "Calculations, site diary, quality inspection & site reports"),
    STRUCTURAL_ENGINEER("Structural Engineer", "Reinforcement calculations, design review & technical specs"),
    QUANTITY_SURVEYOR("Quantity Surveyor", "BOQ analysis, cost estimation, unit rates & payment claims"),
    PROJECT_MANAGER("Project Manager", "Project milestones, schedule, task assignment & risk management"),
    TENDER_OFFICER("Tender / Procurement Officer", "Compliance tracking, bid documents & proposal creation"),
    CONSULTANT("Engineering Consultant", "Specifications, design validation, RFIs & inspection approval"),
    STUDENT("Engineering Student / Intern", "Calculations, reference tools & learning templates")
}

enum class TenderStatus(val label: String, val color: Color) {
    DRAFT("Draft", StatusDraft),
    REVIEWING("Reviewing Docs", StatusReview),
    PREPARING("Preparing Bid", StatusPreparing),
    READY_FOR_SUBMISSION("Ready for Submission", ElectricBlue),
    SUBMITTED("Submitted", StatusSubmitted),
    WON("Won", StatusWon),
    LOST("Lost", StatusLost),
    ARCHIVED("Archived", StatusDraft)
}

enum class ContractType(val label: String) {
    LUMP_SUM("Lump Sum Contract"),
    MEASURED_BOQ("Admeasurement / Measured BOQ"),
    COST_PLUS("Cost Plus Fee"),
    DESIGN_BUILD("Design & Build (D&B)"),
    FIDIC_RED("FIDIC Red Book (Construction)"),
    FIDIC_YELLOW("FIDIC Yellow Book (Plant & Design-Build)"),
    EPC_TURNKEY("EPC / Turnkey")
}

enum class ProjectStatus(val label: String, val color: Color) {
    PLANNING("Planning & Mobilization", StatusReview),
    IN_PROGRESS("In Progress", StatusPreparing),
    ON_HOLD("On Hold", StatusReview),
    COMPLETED("Completed", StatusWon),
    DEFECTS_LIABILITY("Defects Liability Period", ElectricBlue)
}

enum class DocumentCategory(val label: String) {
    TENDER_DOC("Tender Documents"),
    COMPANY_DOC("Company Documents"),
    TECHNICAL_SPEC("Technical Specifications"),
    BOQ("Bill of Quantities (BOQ)"),
    DRAWING("Engineering Drawings"),
    FINANCIAL("Financial & Bank Records"),
    SITE_REPORT("Site Reports & Diary"),
    CONTRACT("Contracts & Agreements"),
    CERTIFICATE("Licenses & Certificates"),
    GENERAL("General Documents")
}

enum class AIMode(val title: String, val description: String) {
    TENDER_ASSISTANT("Tender Assistant", "Summarize tender specs, extract requirements & analyze compliance"),
    ENGINEERING_ASSISTANT("Engineering Assistant", "Resolve technical specs, formulas, concrete mixes & codes"),
    SITE_ASSISTANT("Site Assistant", "Generate daily site reports, safety logs & risk assessments"),
    PROJECT_ASSISTANT("Project Assistant", "Draft work programmes, progress updates & task breakdowns"),
    DOCUMENT_ASSISTANT("Document Assistant", "Review contracts, technical proposals & method statements"),
    GENERAL("General Civil AI", "General civil engineering and construction Q&A")
}

enum class CalculationCategory(val title: String) {
    CONCRETE("Concrete & Mix"),
    EARTHWORKS("Excavation & Backfill"),
    MASONRY("Brick & Blockwork"),
    REINFORCEMENT("Steel Rebar & Weight"),
    HYDRAULICS("Pipe Flow & Tanks"),
    CONVERSIONS("Engineering Conversions")
}
