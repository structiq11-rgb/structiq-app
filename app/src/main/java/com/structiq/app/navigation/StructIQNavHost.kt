package com.structiq.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.structiq.app.core.designsystem.theme.CivilOrange
import com.structiq.app.data.repository.*
import com.structiq.app.feature.ai.*
import com.structiq.app.feature.documents.*
import com.structiq.app.feature.home.*
import com.structiq.app.feature.onboarding.*
import com.structiq.app.feature.projects.*
import com.structiq.app.feature.tenders.*
import com.structiq.app.feature.templates.*
import com.structiq.app.feature.tools.*

@Composable
fun MainShellScreen(
    homeViewModel: HomeViewModel,
    tendersViewModel: TendersViewModel,
    projectsViewModel: ProjectsViewModel,
    documentsViewModel: DocumentsViewModel,
    toolsViewModel: ToolsViewModel,
    onNavigateToCreateTender: () -> Unit,
    onNavigateToTenderDetail: (String) -> Unit,
    onNavigateToCreateProject: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit,
    onNavigateToCalculator: (String) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToTemplates: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CivilOrange,
                            selectedTextColor = CivilOrange,
                            indicatorColor = CivilOrange.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToTenders = { navController.navigate(Screen.Tenders.route) },
                    onNavigateToCreateTender = onNavigateToCreateTender,
                    onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                    onNavigateToCreateProject = onNavigateToCreateProject,
                    onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                    onNavigateToTools = { navController.navigate(Screen.Tools.route) },
                    onNavigateToCalculator = onNavigateToCalculator,
                    onNavigateToAI = onNavigateToAI,
                    onNavigateToTemplates = onNavigateToTemplates
                )
            }

            composable(Screen.Tenders.route) {
                TendersScreen(
                    viewModel = tendersViewModel,
                    onCreateTenderClick = onNavigateToCreateTender,
                    onTenderClick = onNavigateToTenderDetail
                )
            }

            composable(Screen.Projects.route) {
                ProjectsScreen(
                    viewModel = projectsViewModel,
                    onCreateProjectClick = onNavigateToCreateProject,
                    onProjectClick = onNavigateToProjectDetail
                )
            }

            composable(Screen.Documents.route) {
                DocumentsScreen(viewModel = documentsViewModel)
            }

            composable(Screen.Tools.route) {
                ToolsScreen(
                    viewModel = toolsViewModel,
                    onCalculatorClick = onNavigateToCalculator
                )
            }
        }
    }
}

@Composable
fun StructIQNavHost(
    navController: NavHostController,
    userRepository: UserRepository,
    tenderRepository: TenderRepository,
    projectRepository: ProjectRepository,
    documentRepository: DocumentRepository,
    calculationRepository: CalculationRepository
) {
    val homeViewModel = remember { HomeViewModel(userRepository, tenderRepository, projectRepository, documentRepository) }
    val tendersViewModel = remember { TendersViewModel(tenderRepository) }
    val projectsViewModel = remember { ProjectsViewModel(projectRepository) }
    val documentsViewModel = remember { DocumentsViewModel(documentRepository) }
    val toolsViewModel = remember { ToolsViewModel(calculationRepository) }
    val onboardingViewModel = remember { OnboardingViewModel(userRepository) }
    val aiViewModel = remember { AIAssistantViewModel() }

    NavHost(
        navController = navController,
        startDestination = Screen.MainShell.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onComplete = {
                    navController.navigate(Screen.MainShell.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MainShell.route) {
            MainShellScreen(
                homeViewModel = homeViewModel,
                tendersViewModel = tendersViewModel,
                projectsViewModel = projectsViewModel,
                documentsViewModel = documentsViewModel,
                toolsViewModel = toolsViewModel,
                onNavigateToCreateTender = { navController.navigate(Screen.CreateTender.route) },
                onNavigateToTenderDetail = { tenderId -> navController.navigate(Screen.TenderDetail.createRoute(tenderId)) },
                onNavigateToCreateProject = { navController.navigate(Screen.CreateProject.route) },
                onNavigateToProjectDetail = { projectId -> navController.navigate(Screen.ProjectDetail.createRoute(projectId)) },
                onNavigateToCalculator = { calcType -> navController.navigate(Screen.CalculatorDetail.createRoute(calcType)) },
                onNavigateToAI = { navController.navigate(Screen.AIAssistant.route) },
                onNavigateToTemplates = { navController.navigate(Screen.Templates.route) }
            )
        }

        composable(Screen.CreateTender.route) {
            CreateTenderScreen(
                viewModel = tendersViewModel,
                onBackClick = { navController.popBackStack() },
                onTenderCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.TenderDetail.route) { backStackEntry ->
            val tenderId = backStackEntry.arguments?.getString("tenderId") ?: ""
            val detailViewModel = remember(tenderId) { TenderDetailViewModel(tenderId, tenderRepository, documentRepository) }
            TenderDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateProject.route) {
            CreateProjectScreen(
                viewModel = projectsViewModel,
                onBackClick = { navController.popBackStack() },
                onProjectCreated = { navController.popBackStack() }
            )
        }

        composable(Screen.ProjectDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            val detailViewModel = remember(projectId) { ProjectDetailViewModel(projectId, projectRepository) }
            ProjectDetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CalculatorDetail.route) { backStackEntry ->
            val calcType = backStackEntry.arguments?.getString("calcType") ?: "concrete"
            CalculatorDetailScreen(
                calcType = calcType,
                viewModel = toolsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AIAssistant.route) {
            AIAssistantScreen(
                viewModel = aiViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Templates.route) {
            TemplatesScreen(
                onTemplateClick = { templateId ->
                    if (templateId == "method_statement") {
                        navController.navigate(Screen.MethodStatementBuilder.route)
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.MethodStatementBuilder.route) {
            MethodStatementBuilderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
