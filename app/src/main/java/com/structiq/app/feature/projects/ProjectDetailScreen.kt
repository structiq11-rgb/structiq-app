package com.structiq.app.feature.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.structiq.app.core.designsystem.components.*
import com.structiq.app.core.designsystem.theme.CivilOrange
import com.structiq.app.core.designsystem.theme.ElectricBlue
import com.structiq.app.core.designsystem.theme.EmeraldSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    onBackClick: () -> Unit
) {
    val project by viewModel.project.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val siteReports by viewModel.siteReports.collectAsState()
    val issues by viewModel.issues.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Overview", "Tasks", "Site Diary", "Issues")

    var showAddReportDialog by remember { mutableStateOf(false) }
    var weatherInput by remember { mutableStateOf("Clear & Sunny (26°C)") }
    var labourInput by remember { mutableStateOf("18") }
    var equipmentInput by remember { mutableStateOf("1x Excavator 20T, 1x Plate Compactor") }
    var activitiesInput by remember { mutableStateOf("Poured 25m³ C25 concrete for trench bed") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "Project Workspace") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        project?.let { proj ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Top Tab Bar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> { // Overview Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EngineeringCard {
                                Text(
                                    text = proj.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Client: ${proj.clientName} | Location: ${proj.location}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Contract Value: \$${"%,.0f".format(proj.projectValueUsd)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ElectricBlue
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Overall Construction Progress",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${(proj.progressPercent * 100).toInt()}% Complete",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = ElectricBlue
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = proj.progressPercent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = ElectricBlue,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }

                    1 -> { // Tasks Tab
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(tasks) { task ->
                                EngineeringCard(
                                    onClick = { viewModel.toggleTask(task.id) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (task.isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Assigned: ${task.assignee} • Priority: ${task.priority}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> { // Site Diary Tab
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daily Site Reports",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Button(
                                    onClick = { showAddReportDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = CivilOrange)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Site Report")
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(siteReports) { report ->
                                    EngineeringCard {
                                        Text(
                                            text = "Site Report • ${report.weatherCondition}",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = CivilOrange
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "• Labour Force: ${report.labourCount} Workers on site", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "• Equipment: ${report.equipmentSummary}", style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "Activities: ${report.activitiesCompleted}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Safety: ${report.safetyObservations}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    3 -> { // Issues Tab
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(issues) { issue ->
                                EngineeringCard {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = issue.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (issue.severity == "Critical") Color(0xFFEF4444).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = issue.severity,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (issue.severity == "Critical") Color(0xFFEF4444) else Color(0xFFF59E0B)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Status: ${issue.status} • Logged: ${issue.dateIdentified}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // New Site Report Modal
            if (showAddReportDialog) {
                AlertDialog(
                    onDismissRequest = { showAddReportDialog = false },
                    title = { Text("Log Daily Site Report") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = weatherInput,
                                onValueChange = { weatherInput = it },
                                label = { Text("Weather Condition & Temp") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = labourInput,
                                onValueChange = { labourInput = it },
                                label = { Text("Labour Count") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = activitiesInput,
                                onValueChange = { activitiesInput = it },
                                label = { Text("Activities Completed") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.addSiteReport(
                                    weather = weatherInput,
                                    labourCount = labourInput.toIntOrNull() ?: 10,
                                    equipment = equipmentInput,
                                    activities = activitiesInput,
                                    safety = "Standard safety compliance verified"
                                )
                                showAddReportDialog = false
                            }
                        ) {
                            Text("Save Report")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddReportDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
