package com.structiq.app.feature.tenders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import com.structiq.app.core.designsystem.theme.EmeraldSuccess
import com.structiq.app.core.model.TenderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderDetailScreen(
    viewModel: TenderDetailViewModel,
    onBackClick: () -> Unit
) {
    val tender by viewModel.tender.collectAsState()
    val requirements by viewModel.requirements.collectAsState()

    var selectedTabCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Administrative", "Technical", "Financial", "Submission")

    var expandedStatusMenu by remember { mutableStateOf(false) }

    val filteredReqs = remember(requirements, selectedTabCategory) {
        if (selectedTabCategory == "All") requirements else requirements.filter { it.category == selectedTabCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tender?.title ?: "Tender Workspace") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        tender?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Top Summary Header Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Interactive Status Menu
                        ExposedDropdownMenuBox(
                            expanded = expandedStatusMenu,
                            onExpandedChange = { expandedStatusMenu = !expandedStatusMenu }
                        ) {
                            Box(modifier = Modifier.menuAnchor()) {
                                StatusBadge(status = t.status)
                            }
                            ExposedDropdownMenu(
                                expanded = expandedStatusMenu,
                                onDismissRequest = { expandedStatusMenu = false }
                            ) {
                                TenderStatus.values().forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st.label) },
                                        onClick = {
                                            viewModel.updateStatus(st)
                                            expandedStatusMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        val daysLeft = ((t.closingDateEpochMs - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                        Text(
                            text = "Closing: $daysLeft days left",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (daysLeft <= 7) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Client: ${t.clientName} | Tender No: ${t.tenderNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Contract Type: ${t.contractType.label} | Est. \$${"%,.0f".format(t.estimatedValueUsd)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Compliance Progress Meter
                    val progress = if (t.totalRequirementsCount > 0) {
                        t.completedRequirementsCount.toFloat() / t.totalRequirementsCount.toFloat()
                    } else 0f

                    EngineeringCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bid Compliance Progress",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% Ready",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldSuccess
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = EmeraldSuccess,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${t.completedRequirementsCount} of ${t.totalRequirementsCount} mandatory items verified",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Requirement Categories Tab Bar
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedTabCategory),
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    categories.forEach { cat ->
                        Tab(
                            selected = selectedTabCategory == cat,
                            onClick = { selectedTabCategory = cat },
                            text = { Text(cat, fontWeight = if (selectedTabCategory == cat) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                // Checklist Items
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredReqs) { req ->
                        EngineeringCard(
                            onClick = { viewModel.toggleRequirement(req.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (req.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (req.isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(26.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = req.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = req.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = req.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    req.attachedDocName?.let { docName ->
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Description, contentDescription = null, tint = CivilOrange, modifier = Modifier.size(16.dp))
                                            Text(
                                                text = "Attached: $docName",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = CivilOrange
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
