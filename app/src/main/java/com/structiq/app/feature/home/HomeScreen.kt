package com.structiq.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.structiq.app.core.designsystem.components.*
import com.structiq.app.core.designsystem.theme.CivilOrange
import com.structiq.app.core.designsystem.theme.ElectricBlue
import com.structiq.app.core.designsystem.theme.EmeraldSuccess

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTenders: () -> Unit,
    onNavigateToCreateTender: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToCreateProject: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToCalculator: (String) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToTemplates: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val tenders by viewModel.tenders.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val documents by viewModel.documents.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Header Banner
        StructIQHeader(
            title = "Hello, ${userProfile?.fullName ?: "Engineer"}",
            subtitle = userProfile?.companyName ?: "Civil & Construction Workspace",
            onAiClick = onNavigateToAI
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Action Bar
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    QuickActionChip("New Tender", Icons.Default.AddTask, CivilOrange, onNavigateToCreateTender)
                }
                item {
                    QuickActionChip("New Project", Icons.Default.Engineering, ElectricBlue, onNavigateToCreateProject)
                }
                item {
                    QuickActionChip("Calculators", Icons.Default.Calculate, EmeraldSuccess, onNavigateToTools)
                }
                item {
                    QuickActionChip("Templates", Icons.Default.Description, Color(0xFFA855F7), onNavigateToTemplates)
                }
                item {
                    QuickActionChip("AI Assistant", Icons.Default.AutoAwesome, CivilOrange, onNavigateToAI)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Active Tenders Overview
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Tenders (${tenders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToTenders) {
                    Text("View All", color = CivilOrange)
                }
            }

            if (tenders.isEmpty()) {
                EngineeringCard {
                    Text(
                        text = "No active tenders yet. Create your first tender workspace to track requirements & compliance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                tenders.take(2).forEach { tender ->
                    EngineeringCard(
                        modifier = Modifier.padding(bottom = 10.dp),
                        onClick = onNavigateToTenders
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(status = tender.status)
                            val daysLeft = ((tender.closingDateEpochMs - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                            Text(
                                text = "Closing in $daysLeft days",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (daysLeft <= 7) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = tender.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Client: ${tender.clientName} | Est: \$${"%,.0f".format(tender.estimatedValueUsd)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Requirements Progress Bar
                        val progress = if (tender.totalRequirementsCount > 0) {
                            tender.completedRequirementsCount.toFloat() / tender.totalRequirementsCount.toFloat()
                        } else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CivilOrange,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% Compliance",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active Projects Overview
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Construction Projects (${projects.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onNavigateToProjects) {
                    Text("View All", color = ElectricBlue)
                }
            }

            projects.take(2).forEach { proj ->
                EngineeringCard(
                    modifier = Modifier.padding(bottom = 10.dp),
                    onClick = onNavigateToProjects
                ) {
                    Text(
                        text = proj.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Location: ${proj.location} | Value: \$${"%,.0f".format(proj.projectValueUsd)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = proj.progressPercent,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ElectricBlue,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Text(
                            text = "${(proj.progressPercent * 100).toInt()}% Done",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Engineering Toolbox Shortcut Section
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Engineering Toolbox",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EngineeringCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToCalculator("concrete") }
                ) {
                    Icon(Icons.Default.SquareFoot, contentDescription = null, tint = EmeraldSuccess)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Concrete Mix", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Volume, cement, sand & aggregates", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                EngineeringCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToCalculator("rebar") }
                ) {
                    Icon(Icons.Default.GridOn, contentDescription = null, tint = CivilOrange)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Steel Rebar", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Weight tonnage & length (D²/162)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
