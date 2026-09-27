package com.structiq.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object MainShell : Screen("main_shell")
    object Home : Screen("home")
    object Tenders : Screen("tenders")
    object CreateTender : Screen("create_tender")
    object TenderDetail : Screen("tender_detail/{tenderId}") {
        fun createRoute(tenderId: String) = "tender_detail/$tenderId"
    }
    object Projects : Screen("projects")
    object CreateProject : Screen("create_project")
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    object Documents : Screen("documents")
    object Tools : Screen("tools")
    object CalculatorDetail : Screen("calculator_detail/{calcType}") {
        fun createRoute(calcType: String) = "calculator_detail/$calcType"
    }
    object AIAssistant : Screen("ai_assistant")
    object Templates : Screen("templates")
    object MethodStatementBuilder : Screen("method_statement_builder")
    object CompanyProfileBuilder : Screen("company_profile_builder")
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem(Screen.Tenders.route, "Tenders", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    BottomNavItem(Screen.Projects.route, "Projects", Icons.Filled.Engineering, Icons.Outlined.Engineering),
    BottomNavItem(Screen.Documents.route, "Documents", Icons.Filled.Folder, Icons.Outlined.Folder),
    BottomNavItem(Screen.Tools.route, "Tools", Icons.Filled.Calculate, Icons.Outlined.Calculate)
)
