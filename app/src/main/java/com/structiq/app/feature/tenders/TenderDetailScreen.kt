package com.structiq.app.feature.tenders

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.structiq.app.core.database.TenderChecklistItemEntity
import com.structiq.app.core.designsystem.components.*
import com.structiq.app.core.designsystem.theme.*
import com.structiq.app.core.model.TenderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderDetailScreen(
    viewModel: TenderDetailViewModel,
    onBackClick: () -> Unit
) {
    val tender by viewModel.tender.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()
    val readiness by viewModel.readinessSummary.collectAsState()
    val tenderNotes by viewModel.tenderNotes.collectAsState()
    val tenderDocs by viewModel.tenderDocuments.collectAsState()

    var selectedMainTab by remember { mutableStateOf(0) } // 0: Checklist, 1: Notes, 2: Documents
    val mainTabs = listOf("Bid Checklist", "Tender Notes", "Documents")

    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Administrative", "Technical", "Financial", "Submission")

    var expandedStatusMenu by remember { mutableStateOf(false) }

    // Dialog States
    var showAddChecklistDialog by remember { mutableStateOf(false) }
    var newReqTitle by remember { mutableStateOf("") }
    var newReqCategory by remember { mutableStateOf("Technical") }
    var newReqDesc by remember { mutableStateOf("") }
    var newReqIsCritical by remember { mutableStateOf(false) }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var newNoteText by remember { mutableStateOf("") }
    var newNoteCategory by remember { mutableStateOf("Internal Review") }

    // Document Picker Attachment State
    var targetChecklistItem by remember { mutableStateOf<TenderChecklistItemEntity?>(null) }
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            targetChecklistItem?.let { item ->
                val fileName = selectedUri.lastPathSegment ?: "Attached_Document.pdf"
                viewModel.attachDocumentToRequirement(item, selectedUri, fileName)
            }
        }
    }

    val filteredChecklist = remember(checklistItems, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") checklistItems else checklistItems.filter { it.category == selectedCategoryFilter }
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
        },
        floatingActionButton = {
            if (selectedMainTab == 0) {
                FloatingActionButton(
                    onClick = { showAddChecklistDialog = true },
                    containerColor = CivilOrange,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Requirement")
                }
            } else if (selectedMainTab == 1) {
                FloatingActionButton(
                    onClick = { showAddNoteDialog = true },
                    containerColor = CivilOrange,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.NoteAdd, contentDescription = "Add Note")
                }
            }
        }
    ) { innerPadding ->
        tender?.let { t ->
            val deadlineInfo = viewModel.calculateDeadlineStatus(t.closingDateEpochMs)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header Summary Card & Bid Readiness Meter
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
                                            viewModel.updateTenderStatus(st)
                                            expandedStatusMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Dynamic Color-Coded Deadline Badge
                        val (deadlineState, deadlineText) = deadlineInfo
                        val deadlineColor = when (deadlineState) {
                            DeadlineStatus.GREEN -> EmeraldSuccess
                            DeadlineStatus.AMBER -> AmberWarning
                            DeadlineStatus.RED -> Color(0xFFEF4444)
                            DeadlineStatus.EXPIRED -> Color(0xFF64748B)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(deadlineColor.copy(alpha = 0.15f))
                                .border(1.dp, deadlineColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = deadlineText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = deadlineColor
                            )
                        }
                    }

                    Text(
                        text = "Client: ${t.clientName} | Tender Ref: ${t.tenderNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Contract Type: ${t.contractType.label} | Est: \$${"%,.0f".format(t.estimatedValueUsd)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Persistent Bid Readiness Card
                    EngineeringCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bid Readiness",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${readiness.readinessPercentage}% Complete",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (readiness.readinessPercentage >= 80) EmeraldSuccess else CivilOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = readiness.readinessPercentage / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (readiness.readinessPercentage >= 80) EmeraldSuccess else CivilOrange,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Readiness Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• Completed: ${readiness.completedCount}/${readiness.totalCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "• Critical Outstanding: ${readiness.criticalOutstandingCount}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (readiness.criticalOutstandingCount > 0) Color(0xFFEF4444) else EmeraldSuccess
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• With Docs: ${readiness.countWithDocs}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "• Missing Docs: ${readiness.countWithoutDocs}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Disclaimer Banner
                        Text(
                            text = "*Disclaimer: Bid Readiness helps organize and review tender requirements for submission preparation but cannot guarantee tender evaluation results or award.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Main Section Tabs
                TabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    mainTabs.forEachIndexed { idx, title ->
                        Tab(
                            selected = selectedMainTab == idx,
                            onClick = { selectedMainTab = idx },
                            text = { Text(title, fontWeight = if (selectedMainTab == idx) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                when (selectedMainTab) {
                    0 -> { // Bid Checklist Tab
                        Column(modifier = Modifier.fillMaxSize()) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { cat ->
                                    FilterChip(
                                        selected = selectedCategoryFilter == cat,
                                        onClick = { selectedCategoryFilter = cat },
                                        label = { Text(cat) }
                                    )
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredChecklist) { req ->
                                    EngineeringCard {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            IconButton(onClick = { viewModel.toggleChecklistItem(req) }) {
                                                Icon(
                                                    imageVector = if (req.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                    contentDescription = null,
                                                    tint = if (req.isCompleted) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }

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

                                                    if (req.isCritical) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "CRITICAL",
                                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                                color = Color(0xFFEF4444)
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = req.title,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                Text(
                                                    text = req.description,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                Spacer(modifier = Modifier.height(6.dp))

                                                // Supporting Document Attachment Section
                                                if (!req.supportingDocName.isNullOrBlank()) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = CivilOrange, modifier = Modifier.size(16.dp))
                                                        Text(
                                                            text = "Doc Attached: ${req.supportingDocName}",
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = CivilOrange
                                                        )
                                                    }
                                                } else {
                                                    TextButton(
                                                        onClick = {
                                                            targetChecklistItem = req
                                                            documentPickerLauncher.launch("*/*")
                                                        },
                                                        contentPadding = PaddingValues(0.dp)
                                                    ) {
                                                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Attach Supporting Document", style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> { // Tender Notes Tab
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (tenderNotes.isEmpty()) {
                                item {
                                    Text("No tender notes added yet. Tap '+' to record internal notes or clarifications.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            items(tenderNotes) { note ->
                                EngineeringCard {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CivilOrange.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = note.category,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = CivilOrange
                                            )
                                        }
                                        IconButton(onClick = { viewModel.deleteTenderNote(note) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = note.noteText, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }

                    2 -> { // Associated Documents Tab
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (tenderDocs.isEmpty()) {
                                item {
                                    Text("No tender documents attached yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            items(tenderDocs) { doc ->
                                EngineeringCard {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = CivilOrange)
                                        Column {
                                            Text(text = doc.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            Text(text = "Category: ${doc.category.label}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Custom Checklist Item Dialog
        if (showAddChecklistDialog) {
            AlertDialog(
                onDismissRequest = { showAddChecklistDialog = false },
                title = { Text("Add Tender Requirement") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newReqTitle,
                            onValueChange = { newReqTitle = it },
                            label = { Text("Requirement Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newReqDesc,
                            onValueChange = { newReqDesc = it },
                            label = { Text("Specification Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = newReqIsCritical,
                                onCheckedChange = { newReqIsCritical = it }
                            )
                            Text("Mark as Critical Requirement")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addCustomChecklistItem(
                                category = newReqCategory,
                                title = newReqTitle,
                                description = newReqDesc,
                                isCritical = newReqIsCritical
                            )
                            showAddChecklistDialog = false
                            newReqTitle = ""
                            newReqDesc = ""
                        }
                    ) {
                        Text("Add Requirement")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddChecklistDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Add Tender Note Dialog
        if (showAddNoteDialog) {
            AlertDialog(
                onDismissRequest = { showAddNoteDialog = false },
                title = { Text("Add Tender Note / Clarification") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newNoteText,
                            onValueChange = { newNoteText = it },
                            label = { Text("Note / Instructions Text") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addTenderNote(newNoteText, newNoteCategory)
                            showAddNoteDialog = false
                            newNoteText = ""
                        }
                    ) {
                        Text("Save Note")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddNoteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
