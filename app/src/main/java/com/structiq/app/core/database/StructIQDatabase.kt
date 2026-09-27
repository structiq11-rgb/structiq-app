package com.structiq.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserProfileEntity::class,
        TenderEntity::class,
        ProjectEntity::class,
        DocumentEntity::class,
        SiteReportEntity::class,
        CalculationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StructIQDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun tenderDao(): TenderDao
    abstract fun projectDao(): ProjectDao
    abstract fun documentDao(): DocumentDao
    abstract fun siteReportDao(): SiteReportDao
    abstract fun calculationDao(): CalculationDao

    companion object {
        @Volatile
        private var INSTANCE: StructIQDatabase? = null

        fun getDatabase(context: Context): StructIQDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StructIQDatabase::class.java,
                    "structiq_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
