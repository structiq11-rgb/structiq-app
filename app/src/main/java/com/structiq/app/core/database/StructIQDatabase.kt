package com.structiq.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        TenderEntity::class,
        TenderChecklistItemEntity::class,
        TenderNoteEntity::class,
        ProjectEntity::class,
        ProjectTaskEntity::class,
        SiteDiaryEntryEntity::class,
        ProjectIssueEntity::class,
        DocumentEntity::class,
        SiteReportEntity::class,
        CalculationEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StructIQDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun tenderDao(): TenderDao
    abstract fun tenderChecklistDao(): TenderChecklistDao
    abstract fun tenderNoteDao(): TenderNoteDao
    abstract fun projectDao(): ProjectDao
    abstract fun projectTaskDao(): ProjectTaskDao
    abstract fun siteDiaryEntryDao(): SiteDiaryEntryDao
    abstract fun projectIssueDao(): ProjectIssueDao
    abstract fun documentDao(): DocumentDao
    abstract fun siteReportDao(): SiteReportDao
    abstract fun calculationDao(): CalculationDao

    companion object {
        @Volatile
        private var INSTANCE: StructIQDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tender_checklist_items` (
                        `id` TEXT NOT NULL, 
                        `tenderId` TEXT NOT NULL, 
                        `category` TEXT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `description` TEXT NOT NULL, 
                        `isCompleted` INTEGER NOT NULL, 
                        `isRequired` INTEGER NOT NULL, 
                        `isCritical` INTEGER NOT NULL, 
                        `notes` TEXT NOT NULL, 
                        `supportingDocUri` TEXT, 
                        `supportingDocName` TEXT, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tender_notes` (
                        `id` TEXT NOT NULL, 
                        `tenderId` TEXT NOT NULL, 
                        `noteText` TEXT NOT NULL, 
                        `category` TEXT NOT NULL, 
                        `createdAtEpochMs` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `project_tasks` (
                        `id` TEXT NOT NULL, 
                        `projectId` TEXT NOT NULL, 
                        `taskName` TEXT NOT NULL, 
                        `description` TEXT NOT NULL, 
                        `assignedPerson` TEXT NOT NULL, 
                        `startDateEpochMs` INTEGER NOT NULL, 
                        `dueDateEpochMs` INTEGER NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `progressPercent` REAL NOT NULL, 
                        `priority` TEXT NOT NULL, 
                        `notes` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `site_diary_entries` (
                        `id` TEXT NOT NULL, 
                        `projectId` TEXT NOT NULL, 
                        `entryDateEpochMs` INTEGER NOT NULL, 
                        `weather` TEXT NOT NULL, 
                        `sitePersonnelSummary` TEXT NOT NULL, 
                        `plantEquipmentSummary` TEXT NOT NULL, 
                        `materialsDelivered` TEXT NOT NULL, 
                        `workPerformed` TEXT NOT NULL, 
                        `quantitiesMeasured` TEXT NOT NULL, 
                        `visitors` TEXT NOT NULL, 
                        `instructionsReceived` TEXT NOT NULL, 
                        `delays` TEXT NOT NULL, 
                        `safetyObservations` TEXT NOT NULL, 
                        `qualityObservations` TEXT NOT NULL, 
                        `issues` TEXT NOT NULL, 
                        `generalNotes` TEXT NOT NULL, 
                        `photoUrisJson` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `project_issues` (
                        `id` TEXT NOT NULL, 
                        `projectId` TEXT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `description` TEXT NOT NULL, 
                        `category` TEXT NOT NULL, 
                        `priority` TEXT NOT NULL, 
                        `dateIdentifiedEpochMs` INTEGER NOT NULL, 
                        `assignedPerson` TEXT NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `dueDateEpochMs` INTEGER NOT NULL, 
                        `resolutionNotes` TEXT NOT NULL, 
                        `photoUrisJson` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): StructIQDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StructIQDatabase::class.java,
                    "structiq_database.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
