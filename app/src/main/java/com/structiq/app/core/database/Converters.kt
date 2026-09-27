package com.structiq.app.core.database

import androidx.room.TypeConverter
import com.structiq.app.core.model.*

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CONTRACTOR)

    @TypeConverter
    fun fromTenderStatus(status: TenderStatus): String = status.name

    @TypeConverter
    fun toTenderStatus(value: String): TenderStatus = runCatching { TenderStatus.valueOf(value) }.getOrDefault(TenderStatus.DRAFT)

    @TypeConverter
    fun fromContractType(type: ContractType): String = type.name

    @TypeConverter
    fun toContractType(value: String): ContractType = runCatching { ContractType.valueOf(value) }.getOrDefault(ContractType.MEASURED_BOQ)

    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String = status.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = runCatching { ProjectStatus.valueOf(value) }.getOrDefault(ProjectStatus.PLANNING)

    @TypeConverter
    fun fromDocumentCategory(cat: DocumentCategory): String = cat.name

    @TypeConverter
    fun toDocumentCategory(value: String): DocumentCategory = runCatching { DocumentCategory.valueOf(value) }.getOrDefault(DocumentCategory.GENERAL)

    @TypeConverter
    fun fromCalculationCategory(cat: CalculationCategory): String = cat.name

    @TypeConverter
    fun toCalculationCategory(value: String): CalculationCategory = runCatching { CalculationCategory.valueOf(value) }.getOrDefault(CalculationCategory.CONCRETE)
}
