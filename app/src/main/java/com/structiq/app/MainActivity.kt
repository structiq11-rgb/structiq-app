package com.structiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.structiq.app.core.designsystem.theme.StructIQTheme
import com.structiq.app.data.repository.*
import com.structiq.app.navigation.StructIQNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate()

        val app = application as StructIQApplication
        val database = app.database

        val userRepository = UserRepository(database.userProfileDao())
        val tenderRepository = TenderRepository(database.tenderDao())
        val projectRepository = ProjectRepository(database.projectDao())
        val documentRepository = DocumentRepository(database.documentDao())
        val calcRepository = CalculationRepository(database.calculationDao())

        setContent {
            StructIQTheme {
                val navController = rememberNavController()
                StructIQNavHost(
                    navController = navController,
                    userRepository = userRepository,
                    tenderRepository = tenderRepository,
                    projectRepository = projectRepository,
                    documentRepository = documentRepository,
                    calculationRepository = calcRepository
                )
            }
        }
    }
}
