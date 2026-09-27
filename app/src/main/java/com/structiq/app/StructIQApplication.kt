package com.structiq.app

import android.app.Application
import com.structiq.app.core.database.StructIQDatabase
import com.structiq.app.data.sample.SampleDataGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StructIQApplication : Application() {

    val database: StructIQDatabase by lazy {
        StructIQDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        seedInitialDataIfEmpty()
    }

    private fun seedInitialDataIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            val userProfileDao = database.userProfileDao()
            if (userProfileDao.getUserProfile() == null) {
                userProfileDao.insertOrUpdateProfile(SampleDataGenerator.sampleUserProfile)
                
                val tenderDao = database.tenderDao()
                SampleDataGenerator.sampleTenders.forEach { tenderDao.insertTender(it) }

                val checklistDao = database.tenderChecklistDao()
                SampleDataGenerator.sampleChecklistItems.forEach { checklistDao.insertChecklistItem(it) }

                val noteDao = database.tenderNoteDao()
                SampleDataGenerator.sampleTenderNotes.forEach { noteDao.insertNote(it) }

                val projectDao = database.projectDao()
                SampleDataGenerator.sampleProjects.forEach { projectDao.insertProject(it) }

                val taskDao = database.projectTaskDao()
                SampleDataGenerator.sampleTasks.forEach { taskDao.insertTask(it) }

                val diaryDao = database.siteDiaryEntryDao()
                SampleDataGenerator.sampleDiaryEntries.forEach { diaryDao.insertDiaryEntry(it) }

                val issueDao = database.projectIssueDao()
                SampleDataGenerator.sampleIssues.forEach { issueDao.insertIssue(it) }

                val documentDao = database.documentDao()
                SampleDataGenerator.sampleDocuments.forEach { documentDao.insertDocument(it) }

                val calcDao = database.calculationDao()
                SampleDataGenerator.sampleCalculations.forEach { calcDao.insertCalculation(it) }
            }
        }
    }
}
